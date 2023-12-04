package gov.nysenate.ess.core.service.pec.external.everfi.user;

import com.google.common.base.Strings;
import gov.nysenate.ess.core.dao.pec.everfi.EverfiUserDao;
import gov.nysenate.ess.core.dao.personnel.EmployeeDao;
import gov.nysenate.ess.core.model.pec.everfi.EverfiUserIDs;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.model.personnel.EmployeeNotFoundEx;
import gov.nysenate.ess.core.service.mail.SendMailService;
import gov.nysenate.ess.core.service.pec.external.everfi.EverfiApiClient;
import gov.nysenate.ess.core.service.pec.external.everfi.category.EverfiCategoryLabel;
import gov.nysenate.ess.core.service.pec.external.everfi.category.EverfiCategoryService;
import gov.nysenate.ess.core.service.pec.external.everfi.user.add.EverfiAddUserRequest;
import gov.nysenate.ess.core.service.pec.external.everfi.user.update.EverfiUpdateUserRequest;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class EverfiUserService {
    private static final Logger logger = LoggerFactory.getLogger(EverfiUserService.class);
    private static final int MIN_TEST_ID = 77000;

    private final EverfiApiClient everfiApiClient;
    private final EmployeeDao employeeDao;
    private final EverfiUserDao everfiUserDao;
    private final SendMailService sendMailService;
    private final EverfiCategoryService categoryService;
    private final Map<String, EverfiUser> manualReviewUUIDs = new HashMap<>();
    private List<EverfiUserIDs> ignoredEverfiUserIds;

    @Value("${scheduler.everfi.sync.enabled:false}")
    private boolean everfiSyncEnabled;

    private final List<String> everfiReportEmails;


    @Autowired
    public EverfiUserService(EverfiApiClient everfiApiClient, EmployeeDao employeeDao, EverfiUserDao everfiUserDao,
                             SendMailService sendMailService, EverfiCategoryService categoryService, @Value("${everfi.report.email}") String everfiReportEmailList) {
        this.everfiApiClient = everfiApiClient;
        this.employeeDao = employeeDao;
        this.everfiUserDao = everfiUserDao;
        this.sendMailService = sendMailService;
        this.categoryService = categoryService;
        this.ignoredEverfiUserIds = everfiUserDao.getIgnoredEverfiUserIDs();
        this.everfiReportEmails = Arrays.asList(everfiReportEmailList.replaceAll(" ","").split(","));
    }

    @Scheduled(cron = "${scheduler.everfi.user.update.cron}")
    public void runUpdateMethods() {
        if (everfiSyncEnabled) {
            logger.info("Beginning Everfi Sync");
            //Add new employees to Everfi
            addEmployeesToEverfi(getNewEmployeesToAddToEverfi());
            //Inactivate employees on Everfi if any need to be inactivated
            handleInactivatedEmployeesInEverfi();
            //Update our db with their UUID from Everfi
            getEverfiUserIds();
            logger.info("Completed Everfi Sync");
        }
    }

    public static boolean isValid(Integer empId) {
        return empId != null && empId != 0 && empId < MIN_TEST_ID;
    }

    public void handleInactivatedEmployeesInEverfi() {
        try {
            logger.info("Beginning Everfi deactivation process for inactive employees");
            List<Employee> inactiveEmployees = getRecentlyInactiveEmployees();

            sendEmailToEverfiReportEmails("Employees to be Inactivated",
                    generateEmployeeListString(inactiveEmployees) + "\n\n Some of these users above may have already been deactivated prior to this run");

            for (Employee employee : inactiveEmployees) {
                changeActiveStatusForUserWithEmpID(employee.getEmployeeId(), false);
            }
            logger.info("Completed Everfi Deactivation process for inactive employees");
        }
        catch (Exception e) {
            logger.error("Error occurred when handling inactive employees",e);
        }
    }

    public void changeActiveStatusForUserWithEmpID(int submittedEmpID, boolean status) {
        //Quick check to ensure that the employee is real
        try {
            Employee employee = employeeDao.getEmployeeById(submittedEmpID);
            EverfiUserIDs everfiUserID = everfiUserDao.getEverfiUserIDsWithEmpID(employee.getEmployeeId());
            if (everfiUserID == null) {
                logger.warn( "Couldn't change active status for user. " +
                        "Submitted EMP ID " + submittedEmpID + " does not match any employee in the Everfi UUID records table");
                return;
            }
            changeActiveStatusForUser(everfiUserID, status);
        }
        catch (Exception e) {
            logger.error("An error occurred when changing the active status for a user " + submittedEmpID, e);
        }
    }

    public void changeActiveStatusForUserWithUUID(String submittedUUID, boolean status) {
        try { //Ensure UUID is an everfi UUID
            EverfiUserIDs everfiUserID = everfiUserDao.getEverfiUserIDsWithEverfiUUID(submittedUUID);
            if (everfiUserID == null) {
                logger.warn( "Couldn't change active status for user. Submitted UUID does not match any employee in the Everfi records table");
            }
            changeActiveStatusForUser(everfiUserID, status);
        }
        catch (Exception e) {
            logger.error("An error occurred when changing the active status for a user " + submittedUUID, e);
        }
    }

    /**
     * Returns a list of all new employees that must be added to Everfi. Usually called thru cron
     *
     * @return
     */
    public List<Employee> getNewEmployeesToAddToEverfi() {
        try {
            //Minimal Employee Objects -- Must be converted to full employees
            List<Employee> newEmployees = employeeDao.getNewEmployees();
            List<Employee> completeNewEmployees = new ArrayList<>();
            List<Employee> empsToAddToEverfi = new ArrayList<>();

            for (Employee newEmp : newEmployees) {
                completeNewEmployees.add(employeeDao.getEmployeeById(newEmp.getEmployeeId()));
            }

            for (Employee completeNewEmp : completeNewEmployees) {

                EverfiUserIDs potentialEverfiUserID =
                        everfiUserDao.getEverfiUserIDsWithEmpID(completeNewEmp.getEmployeeId());

                if (potentialEverfiUserID == null && completeNewEmp.getEmail() == null) {
                    logger.info(completeNewEmp.getFullName() + " " + completeNewEmp.getEmployeeId() + " has not been added to Everfi and has a null email so they will be skipped");
                }
                else if (potentialEverfiUserID == null && completeNewEmp.getEmail() != null) {
                    logger.info(completeNewEmp.getFullName() + " " + completeNewEmp.getEmployeeId() + " has not been added to Everfi and has a proper email");
                    empsToAddToEverfi.add(completeNewEmp);
                }
                else {
                    logger.info(completeNewEmp.getFullName() + " " + completeNewEmp.getEmployeeId() + " will be skipped. They have been added to Everfi");
                }

            }

            return empsToAddToEverfi;
        }
        catch (Exception e) {

            logger.error("There was an exception when trying to create the list of new employees");
            return new ArrayList<>();

        }
    }

    public List<Employee> getRecentlyInactiveEmployees() {
        try {
            LocalDateTime oneWeekFromToday = LocalDateTime.now().minusDays(7);
            return employeeDao.getInactivatedEmployeesSinceDate(oneWeekFromToday);
        }
        catch (Exception e) {
            logger.error("There was a problem creating the list of recently deactivated employees");
            return new ArrayList<>();
        }

    }

    /**
     * Entry point to get all Everfi UUID's and EmpIds in the database for future use
     *
     * @throws IOException
     */
    public void getEverfiUserIds() {
        try {
            EverfiUsersRequest request = new EverfiUsersRequest(everfiApiClient, 1, 1000);
            List<EverfiUser> everfiUsers;
            logger.info("Contacting Everfi for User records");
            this.manualReviewUUIDs.clear();

            while (request != null) {
                //Contact everfi api
                everfiUsers = request.getUsers();

                //Process records / insert into db
                handleUserRecords(everfiUsers);

                //Get next batch of records
                request = request.next();
            }
        }
        catch (Exception e) {
            logger.error("There was an exception when attempting to import Everfi UUID's");
        }
        logger.info("Completed Everfi ID import");
    }

    private void changeActiveStatusForUser(EverfiUserIDs everfiUserID, boolean activeStatus) {
        try {
            EverfiSingleUserRequest everfiSingleUserRequest =
                    new EverfiSingleUserRequest(everfiApiClient, everfiUserID.getEverfiUUID());
            EverfiUser everfiUser = everfiSingleUserRequest.getUser();

            if (!everfiUser.isActive() && !activeStatus) {
                try {
                    //Ensure that the employees are actually deactivated
                    everfiUserDao.insertIgnoredID(everfiUser.getUuid(), everfiUser.getEmployeeId());
                    this.ignoredEverfiUserIds = everfiUserDao.getIgnoredEverfiUserIDs();
                }
                catch (DuplicateKeyException e) {
                    // Do nothing, it means they are already deactivated, and ignored
                }
                return; //No need to deactivate someone who is deactivated
            }

            //normalize category labels
            //Normalize labels that the everfi user already has. This prevents null pointer exception
            List<EverfiCategoryLabel> normalizedCategoryLabels = this.categoryService.normalizeUsersCategoryLabel(everfiUser.getUserCategoryLabels());
            everfiUser.setUserCategoryLabels(normalizedCategoryLabels);

            if (activeStatus) {
                logger.info("Reactivating of " + everfiUser.getFirstName() + " " + everfiUser.getLastName() + " " + everfiUser.getUuid() );
                //Set them to true
                EverfiUpdateUserRequest activationStatusRequest = new EverfiUpdateUserRequest(everfiApiClient, everfiUser.getUuid(),
                        everfiUser.getEmployeeId(), everfiUser.getFirstName(), everfiUser.getLastName(), everfiUser.getEmail(),
                        null, everfiUser.getUserCategoryLabels(), true);
                EverfiUser nowActiveUser = activationStatusRequest.updateUser();

                String changedEmail = "";
                if (nowActiveUser.getEmail().endsWith("x")) {
                    changedEmail = nowActiveUser.getEmail().substring(0, nowActiveUser.getEmail().length() - 1);
                }
                else {
                    changedEmail = nowActiveUser.getEmail();
                }

                //change email back
                EverfiUpdateUserRequest changeEmailRequest = new EverfiUpdateUserRequest(everfiApiClient, nowActiveUser.getUuid(),
                        nowActiveUser.getEmployeeId(), nowActiveUser.getFirstName(), nowActiveUser.getLastName(), changedEmail,
                        null, nowActiveUser.getUserCategoryLabels(), true);
                EverfiUser changedEmailUser = changeEmailRequest.updateUser();

                //remove from ignored users
                everfiUserDao.removeIgnoredID(everfiUser.getUuid());
                this.ignoredEverfiUserIds = everfiUserDao.getIgnoredEverfiUserIDs();

            }
            if (!activeStatus) {
                logger.info("Beginning deactivation of " + everfiUser.getFirstName() + " " + everfiUser.getLastName() + " " + everfiUser.getUuid());
                //Change email
                EverfiUpdateUserRequest changeEmailRequest = new EverfiUpdateUserRequest(everfiApiClient, everfiUser.getUuid(),
                        everfiUser.getEmployeeId(), everfiUser.getFirstName(), everfiUser.getLastName(), everfiUser.getEmail() + "x",
                        null, everfiUser.getUserCategoryLabels(), true);
                EverfiUser changedEmailUser = changeEmailRequest.updateUser();

                //Set them to inactive
                EverfiUpdateUserRequest activationStatusRequest = new EverfiUpdateUserRequest(everfiApiClient, changedEmailUser.getUuid(),
                        changedEmailUser.getEmployeeId(), changedEmailUser.getFirstName(), changedEmailUser.getLastName(), changedEmailUser.getEmail(),
                        null, changedEmailUser.getUserCategoryLabels(), false);
                activationStatusRequest.updateUser();

                //send id to dao to ignore
                everfiUserDao.insertIgnoredID(everfiUser.getUuid(), everfiUser.getEmployeeId());
                this.ignoredEverfiUserIds = everfiUserDao.getIgnoredEverfiUserIDs();
            }
        }
        catch (Exception e) {
            logger.error("There was an exception when trying to change the active status of a user " + everfiUserID.getEverfiUUID() + " to an active status of " + activeStatus);
        }
    }

    private void sendEmailToEverfiReportEmails(String subject, String html) {
        for (String email : this.everfiReportEmails) {
            sendEmail(email, subject, html);
        }
    }

    private void sendEmail(String to, String subject, String html) {
        try {
            MimeMessage message = sendMailService.newHtmlMessage(to.trim(),
                    subject, html);
            sendMailService.send(message);
        }
        catch (Exception e) {
            logger.error("There was an error trying to send the Everfi report email ", e);
        }
    }

    private String generateEmployeeListString(List<Employee> emps) {
        StringBuilder employeeListDetails = new StringBuilder();
        for (Employee employee: emps) {
            employeeListDetails.append(" NAME: ").append(employee.getFullName())
                    .append(" EMAIL: ").append(employee.getEmail()).append(" EMPID: ")
                    .append(employee.getEmployeeId()).append("<br>\n");
        }

        if (employeeListDetails.isEmpty()) {
            employeeListDetails = new StringBuilder("There are no employees to perform this operation on");
        }
        return employeeListDetails.toString();
    }

    /**
     * Adds the given Employees to Everfi.
     * These employees should not already exist in Everfi. There are separate methods for updating employee data.
     *
     * @param emps
     */
    public void addEmployeesToEverfi(List<Employee> emps) {

        logger.info("Beginning Everfi add employee process");

        //send email to Everfi report email for new employees
        sendEmailToEverfiReportEmails("New Users Added to Everfi", generateEmployeeListString(emps));

            for (Employee emp : emps) {

                try {
                    if (emp.getEmail() == null || emp.getEmail().isEmpty() ) {
                        logger.info("Skipping new employee to Everfi. Their Email is null or empty" + emp.getFullName() + ", " + emp.getEmployeeId());
                        continue;
                    }
                    logger.info("Adding new employee to Everfi " + emp.getFullName() + ", " + emp.getEmail() + ", " + emp.getEmployeeId());
                    EverfiAddUserRequest addUserRequest = new EverfiAddUserRequest(
                            everfiApiClient, emp.getEmployeeId(), emp.getFirstName(), emp.getLastName(),
                            emp.getEmail(), getOrCreateEmpCategoryLabels(emp, null));
                    EverfiUser newestEverfiUser = addUserRequest.addUser();
                    if (newestEverfiUser != null) {
                        everfiUserDao.insertEverfiUserIDs(newestEverfiUser.getUuid(), emp.getEmployeeId());
                    }
                    else {
                        logger.error("Something odd happened when adding " + emp.getEmployeeId() + " to Everfi. Add User request was executed but returned null");
                    }
                }
                catch (Exception e) {
                    logger.error("There was an exception trying to add a new employee " + emp.getEmployeeId() + " to Everfi" + e);
                }
            }
        logger.info("Completed Everfi add employee process");
    }

    public void updateAllEverfiUsers() {
        try {
            EverfiUsersRequest request = new EverfiUsersRequest(everfiApiClient, 1, 1000);
            List<EverfiUser> everfiUsers;

            while (request != null) {
                //Contact Everfi api for all users & cycle thru them
                everfiUsers = request.getUsers();

                for (EverfiUser everfiUser : everfiUsers) {
                    try {
                        updateEverfiUserWithEmpData(getEmployeeId(everfiUser), everfiUser);
                    } catch (Exception e) {
                        logger.warn("There was an exception when trying to update an Everfi user with employee data " + e);
                    }
                }

                //Get next batch of records
                request = request.next();
            }
        }
        catch (Exception e) {
            logger.error("There was an exception when trying to update all employee records in Everfi");
        }
    }

    /**
     * This method finds and inserts an everfi user UUID with a Senate Emp ID into the database
     *
     * @param everfiUsers
     */
    private void handleUserRecords(List<EverfiUser> everfiUsers) {
        for (EverfiUser everfiUser : everfiUsers) {
            String UUID = everfiUser.getUuid();
            if (isEverfiIdIgnored(UUID)) {
                continue;
            }

            try {
                Integer empId = getEmployeeId(everfiUser);

                if (isValid(empId)) {
                    everfiUserDao.insertEverfiUserIDs(UUID, empId);
                } else {
                    logger.warn("Everfi user with UUID " + UUID + " empid " + empId + " was improperly retrieved");
                }
            } catch (DuplicateKeyException e) {
                //Do nothing, it means we already have the user stored in the DB
            } catch (EmployeeNotFoundEx e) {
                logger.debug("Everfi user with UUID " + UUID + " cannot be matched");
            }
        }
    }

    public boolean isEverfiIdIgnored(String everfiUUID) {
        return this.ignoredEverfiUserIds.stream()
                .anyMatch(ignoredID -> ignoredID.getEverfiUUID().equals(everfiUUID));
    }

    private static boolean areNullOrNonnullAndUnequal(Object o1, Object o2) {
        if (o1 == null) {
            return o2 == null;
        }
        return o2 != null && !o1.equals(o2);
    }

    /**
     * Gets the employee object and then their id base off of the everfi email or emp id on file
     */
    private Integer getEmployeeId(EverfiUser everfiUser) throws EmployeeNotFoundEx {
        Integer empIdById = getEmpIdById(everfiUser);
        Integer empIdByEmail = getEmpIdByEmail(everfiUser);
        if (areNullOrNonnullAndUnequal(empIdById, empIdByEmail)) {
            manualReviewUUIDs.putIfAbsent(everfiUser.getUuid(), everfiUser);
            logger.warn("Everfi user record cannot be matched: " + everfiUser);
            return null;
        }
        if (empIdById == null) {
            updateEverfiUserWithEmpData(empIdByEmail, everfiUser);
        }
        if (empIdByEmail == null) {
            updateEverfiUserWithEmpData(empIdById, everfiUser);
        }
        return ObjectUtils.firstNonNull(empIdById, empIdByEmail);
    }

    private Integer getEmpIdById(EverfiUser everfiUser) {
        int everfiUserEmpID = everfiUser.getEmployeeId();
        if (everfiUserEmpID != 0) {
            try {
                return employeeDao.getEmployeeById(everfiUserEmpID).getEmployeeId();
            } catch (Exception ignored) {}
        }
        return null;
    }

    private Integer getEmpIdByEmail(EverfiUser everfiUser) {
        if (!Strings.isNullOrEmpty(everfiUser.getEmail())) {
            try {
                return employeeDao.getEmployeeByEmail(everfiUser.getEmail()).getEmployeeId();
            } catch (Exception ignored) {}
        }
        return null;
    }

    private void updateEverfiUserWithEmpData(Integer empId, EverfiUser everfiUser) {
        try {
            Employee emp = employeeDao.getEmployeeById(empId);

            String properEmail;

            if (emp.getEmail().endsWith("@nysenate.gov") && everfiUser.getEmail().endsWith("@nysenate.gov")
                    && !emp.getEmail().equals(everfiUser.getEmail())) {
                properEmail = emp.getEmail();
            } else {
                properEmail = everfiUser.getEmail();
            }

            //Normalize category labels that the everfi user already has. This prevents null pointer exception
            List<EverfiCategoryLabel> normalizedCategoryLabels = this.categoryService.normalizeUsersCategoryLabel(everfiUser.getUserCategoryLabels());
            everfiUser.setUserCategoryLabels(normalizedCategoryLabels);

            EverfiUpdateUserRequest updateUserRequest =
                    new EverfiUpdateUserRequest(everfiApiClient, everfiUser.getUuid(), emp.getEmployeeId(),
                            emp.getFirstName(), emp.getLastName(), properEmail, "",
                            getOrCreateEmpCategoryLabels(emp, everfiUser), emp.isActive());
            updateUserRequest.updateUser();

        } catch (Exception e) {
            logger.warn("error " + e);
        }

    }

    private List<EverfiCategoryLabel> getOrCreateEmpCategoryLabels(Employee emp, EverfiUser user) throws IOException {
        List<EverfiCategoryLabel> labels = new ArrayList<>();
        labels.add(categoryService.getAttendLiveLabel(emp)); // This label is always "No" so it will never need to be created.
        labels.add(getOrCreateDepartmentLabel(emp));
        labels.add(categoryService.getRoleLabel(emp)); // All possible roles already exist.
        labels.add(getOrCreateUploadListLabel(user));
        return labels;
    }

    /*
     * Returns a Label for this employee's RCH code. If the label does not exist in Everfi it is created.
     */
    private EverfiCategoryLabel getOrCreateDepartmentLabel(Employee emp) throws IOException {
        EverfiCategoryLabel label = categoryService.getDepartmentLabel(emp);
        if (label == null) {
            return categoryService.createDepartmentLabel(emp.getRespCenterHeadCode());
        } else {
            return label;
        }
    }

    /*
     * Returns a Upload List Label. If "user" has an Upload List label, that label will be returned,
     * otherwise a new one is created and returned.
     *
     * Note: if user is null, a new Upload List will be created.
     */
    private EverfiCategoryLabel getOrCreateUploadListLabel(EverfiUser user) throws IOException {
        EverfiCategoryLabel label = categoryService.getUserUploadListLabel(user);
        if (label == null) {
            // user was null or user does not have a Upload List label.

            // Check if a label already exists for today.
            label = categoryService.getUploadListLabel(LocalDate.now());
            if (label == null) {
                // No label exists

                // Create a new Upload List label for today.
                label = categoryService.createUploadListLabel(LocalDate.now());
            }
        }
        return label;
    }
}
