package gov.nysenate.ess.core.service.pec.external.everfi.user;

import com.google.common.base.Strings;
import gov.nysenate.ess.core.dao.pec.everfi.EverfiEmployeeMappingDao;
import gov.nysenate.ess.core.dao.personnel.EmployeeDao;
import gov.nysenate.ess.core.model.pec.everfi.EverfiEmployeeMapping;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.model.personnel.EmployeeNotFoundEx;
import gov.nysenate.ess.core.service.mail.SendMailService;
import gov.nysenate.ess.core.service.pec.external.everfi.category.EverfiCategoryLabel;
import gov.nysenate.ess.core.service.pec.external.everfi.category.EverfiCategoryService;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import jakarta.mail.internet.MimeMessage;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class EverfiUserService {
    private static final Logger logger = LoggerFactory.getLogger(EverfiUserService.class);
    private static final int MIN_TEST_ID = 77000;

    private final EverfiUserClient everfiUserClient;
    private final EmployeeDao employeeDao;
    private final EverfiEmployeeMappingDao everfiEmployeeMappingDao;
    private final SendMailService sendMailService;
    private final EverfiCategoryService categoryService;
    private final Map<String, EverfiUser> manualReviewUUIDs = new HashMap<>();

    @Value("${scheduler.everfi.sync.enabled:false}")
    private boolean everfiSyncEnabled;

    private final List<String> pecAdminReportEmails;


    @Autowired
    public EverfiUserService(EverfiUserClient everfiUserClient, EmployeeDao employeeDao, EverfiEmployeeMappingDao everfiEmployeeMappingDao,
                             SendMailService sendMailService, EverfiCategoryService categoryService, @Value("${pec.admin.report.email}") String pecAdminReportEmails) {
        this.everfiUserClient = everfiUserClient;
        this.employeeDao = employeeDao;
        this.everfiEmployeeMappingDao = everfiEmployeeMappingDao;
        this.sendMailService = sendMailService;
        this.categoryService = categoryService;
        this.pecAdminReportEmails = Arrays.asList(pecAdminReportEmails.replaceAll(" ", "").split(","));
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

            sendEmailToPecAdminReportEmails("Employees to be Inactivated",
                    generateEmployeeListString(inactiveEmployees) + "\n\n Some of these users above may have already been deactivated prior to this run");

            for (Employee employee : inactiveEmployees) {
                everfiEmployeeMappingDao.findByEmpId(employee.getEmployeeId()).ifPresentOrElse(
                        this::deactivateUser,
                        () -> {
                            logger.warn(
                                    "Couldn't change active status for user. Submitted EMP ID {} does not match any employee in the Everfi UUID records table",
                                    employee.getEmployeeId()
                            );
                        }
                );
            }
            logger.info("Completed Everfi Deactivation process for inactive employees");
        } catch (Exception e) {
            logger.error("Error occurred when handling inactive employees", e);
        }
    }

    public void changeActiveStatusForUserWithUUID(String submittedUuid, boolean status) {
        try {
            Optional<EverfiEmployeeMapping> mappingOpt = everfiEmployeeMappingDao.findByEverfiUuid(submittedUuid);

            if (mappingOpt.isEmpty()) {
                logger.warn("Couldn't change active status for user. Submitted UUID does not match any employee in the Everfi records table");
                return;
            }

            EverfiEmployeeMapping mapping = mappingOpt.get();
            if (status) {
                reactivateUser(mapping);
            } else {
                deactivateUser(mapping);
            }
        } catch (Exception e) {
            logger.error("An error occurred when changing the active status for a user " + submittedUuid, e);
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

                EverfiEmployeeMapping potentialEverfiUserID =
                        everfiEmployeeMappingDao.findByEmpId(completeNewEmp.getEmployeeId()).orElse(null);

                if (potentialEverfiUserID == null && completeNewEmp.getEmail() == null) {
                    logger.info(completeNewEmp.getFullName() + " " + completeNewEmp.getEmployeeId() + " has not been added to Everfi and has a null email so they will be skipped");
                } else if (potentialEverfiUserID == null && completeNewEmp.getEmail() != null) {
                    logger.info(completeNewEmp.getFullName() + " " + completeNewEmp.getEmployeeId() + " has not been added to Everfi and has a proper email");
                    empsToAddToEverfi.add(completeNewEmp);
                } else {
                    logger.info(completeNewEmp.getFullName() + " " + completeNewEmp.getEmployeeId() + " will be skipped. They have been added to Everfi");
                }

            }

            return empsToAddToEverfi;
        } catch (Exception e) {

            logger.error("There was an exception when trying to create the list of new employees");
            return new ArrayList<>();

        }
    }

    public List<Employee> getRecentlyInactiveEmployees() {
        try {
            LocalDateTime oneWeekFromToday = LocalDateTime.now().minusDays(7);
            return employeeDao.getInactivatedEmployeesSinceDate(oneWeekFromToday);
        } catch (Exception e) {
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
            logger.info("Contacting Everfi for User records");
            this.manualReviewUUIDs.clear();
            everfiUserClient.forEachPage(this::processEverfiUserRecords);
        } catch (Exception e) {
            logger.error("There was an exception when attempting to import Everfi UUID's");
        }
        logger.info("Completed Everfi ID import");
    }

    private void deactivateUser(EverfiEmployeeMapping everfiUserID) {
        try {
            EverfiUser everfiUser = everfiUserClient.findByUuid(everfiUserID.everfiUuid());

            if (!everfiUser.isActive()) {
                // User is already deactivated in Everfi.
                return;
            }

            logger.info("Beginning deactivation of " + everfiUser.getFirstName() + " " + everfiUser.getLastName() + " " + everfiUser.getUuid());
            //Change email
            EverfiUser changedEmailUser = everfiUserClient.updateUser(EverfiUpdateUserCommand.builder()
                    .uuid(everfiUser.getUuid())
                    .employeeId(requireEverfiEmployeeId(everfiUser))
                    .firstName(everfiUser.getFirstName())
                    .lastName(everfiUser.getLastName())
                    .email(everfiUser.getEmail() + "x")
                    .ssoId(null)
                    .categoryLabels(everfiUser.getCategoryLabels())
                    .active(true)
                    .build());

            //Set them to inactive
            everfiUserClient.updateUser(EverfiUpdateUserCommand.builder()
                    .uuid(changedEmailUser.getUuid())
                    .employeeId(requireEverfiEmployeeId(changedEmailUser))
                    .firstName(changedEmailUser.getFirstName())
                    .lastName(changedEmailUser.getLastName())
                    .email(changedEmailUser.getEmail())
                    .ssoId(null)
                    .categoryLabels(changedEmailUser.getCategoryLabels())
                    .active(false)
                    .build());
        } catch (Exception e) {
            logger.error("There was an exception when trying to deactivate user with everfiId: " + everfiUserID.everfiUuid());
        }
    }

    private void reactivateUser(EverfiEmployeeMapping everfiUserID) {
        try {
            EverfiUser everfiUser = everfiUserClient.findByUuid(everfiUserID.everfiUuid());

            logger.info("Reactivating of " + everfiUser.getFirstName() + " " + everfiUser.getLastName() + " " + everfiUser.getUuid());
            //Set them to true
            EverfiUser nowActiveUser = everfiUserClient.updateUser(EverfiUpdateUserCommand.builder()
                    .uuid(everfiUser.getUuid())
                    .employeeId(requireEverfiEmployeeId(everfiUser))
                    .firstName(everfiUser.getFirstName())
                    .lastName(everfiUser.getLastName())
                    .email(everfiUser.getEmail())
                    .ssoId(null)
                    .categoryLabels(everfiUser.getCategoryLabels())
                    .active(true)
                    .build());

            String changedEmail = "";
            if (nowActiveUser.getEmail().endsWith("x")) {
                changedEmail = nowActiveUser.getEmail().substring(0, nowActiveUser.getEmail().length() - 1);
            } else {
                changedEmail = nowActiveUser.getEmail();
            }

            //change email back
            everfiUserClient.updateUser(EverfiUpdateUserCommand.builder()
                    .uuid(nowActiveUser.getUuid())
                    .employeeId(requireEverfiEmployeeId(nowActiveUser))
                    .firstName(nowActiveUser.getFirstName())
                    .lastName(nowActiveUser.getLastName())
                    .email(changedEmail)
                    .ssoId(null)
                    .categoryLabels(nowActiveUser.getCategoryLabels())
                    .active(true)
                    .build());
        } catch (Exception e) {
            logger.error("There was an exception when trying to reactivate user with everfiId: " + everfiUserID.everfiUuid());
        }
    }

    private void sendEmailToPecAdminReportEmails(String subject, String html) {
        for (String email : this.pecAdminReportEmails) {
            sendEmail(email, subject, html);
        }
    }

    private void sendEmail(String to, String subject, String html) {
        try {
            MimeMessage message = sendMailService.newHtmlMessage(to.trim(),
                    subject, html);
            sendMailService.send(message);
        } catch (Exception e) {
            logger.error("There was an error trying to send the Everfi report email ", e);
        }
    }

    private String generateEmployeeListString(List<Employee> emps) {
        StringBuilder employeeListDetails = new StringBuilder();
        for (Employee employee : emps) {
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
        sendEmailToPecAdminReportEmails("New Users Added to Everfi", generateEmployeeListString(emps));

        for (Employee emp : emps) {

            try {
                if (emp.getEmail() == null || emp.getEmail().isEmpty()) {
                    logger.info("Skipping new employee to Everfi. Their Email is null or empty" + emp.getFullName() + ", " + emp.getEmployeeId());
                    continue;
                }
                logger.info("Adding new employee to Everfi " + emp.getFullName() + ", " + emp.getEmail() + ", " + emp.getEmployeeId());
                EverfiUser newestEverfiUser = everfiUserClient.addUser(EverfiAddUserCommand.builder()
                        .employeeId(emp.getEmployeeId())
                        .firstName(emp.getFirstName())
                        .lastName(emp.getLastName())
                        .email(emp.getEmail())
                        .categoryLabels(getOrCreateEmpCategoryLabels(emp, null))
                        .build());
                if (newestEverfiUser != null) {
                    everfiEmployeeMappingDao.insert(new EverfiEmployeeMapping(emp.getEmployeeId(), newestEverfiUser.getUuid()));
                } else {
                    logger.error("Something odd happened when adding " + emp.getEmployeeId() + " to Everfi. Add User request was executed but returned null");
                }
            } catch (Exception e) {
                logger.error("There was an exception trying to add a new employee " + emp.getEmployeeId() + " to Everfi" + e);
            }
        }
        logger.info("Completed Everfi add employee process");
    }

    public void updateAllEverfiUsers() {
        try {
            everfiUserClient.forEachPage(everfiUsers -> {
                for (EverfiUser everfiUser : everfiUsers) {
                    try {
                        updateEverfiUserWithEmpData(getEmployeeId(everfiUser), everfiUser);
                    } catch (Exception e) {
                        logger.warn("There was an exception when trying to update an Everfi user with employee data " + e);
                    }
                }
            });
        } catch (Exception e) {
            logger.error("There was an exception when trying to update all employee records in Everfi");
        }
    }

    /**
     * This method finds and inserts an everfi user UUID with a Senate Emp ID into the database
     *
     * @param everfiUsers
     */
    private void processEverfiUserRecords(List<EverfiUser> everfiUsers) {
        for (EverfiUser everfiUser : everfiUsers) {
            if (!everfiUser.isActive()) {
                continue;
            }

            try {
                Integer empId = getEmployeeId(everfiUser);

                if (isValid(empId)) {
                    everfiEmployeeMappingDao.insert(new EverfiEmployeeMapping(empId, everfiUser.getUuid()));
                } else {
                    logger.warn("Everfi user with UUID " + everfiUser.getUuid() + " empid " + empId + " was improperly retrieved");
                }
            } catch (DuplicateKeyException e) {
                //Do nothing, it means we already have the user stored in the DB
            } catch (EmployeeNotFoundEx e) {
                logger.debug("Everfi user with UUID " + everfiUser.getUuid() + " cannot be matched");
            }
        }
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
        Integer everfiUserEmpID = everfiUser.getEmployeeId();
        if (everfiUserEmpID != null) {
            try {
                return employeeDao.getEmployeeById(everfiUserEmpID).getEmployeeId();
            } catch (Exception ignored) {
            }
        }
        return null;
    }

    private int requireEverfiEmployeeId(EverfiUser everfiUser) {
        Integer employeeId = everfiUser.getEmployeeId();
        Assert.notNull(employeeId, "Everfi user is missing employeeId");
        Assert.isTrue(employeeId > 0, "Everfi user employeeId must be greater than 0");
        return employeeId;
    }

    private Integer getEmpIdByEmail(EverfiUser everfiUser) {
        if (!Strings.isNullOrEmpty(everfiUser.getEmail())) {
            try {
                return employeeDao.getEmployeeByEmail(everfiUser.getEmail()).getEmployeeId();
            } catch (Exception ignored) {
            }
        }
        return null;
    }

    /**
     * Update everfi with emp data from SFMS, preferring to keep the everfi email if there is a difference.
     *
     * @param empId
     * @param everfiUser
     */
    private void updateEverfiUserWithEmpData(Integer empId, EverfiUser everfiUser) {
        try {
            Employee emp = employeeDao.getEmployeeById(empId);
            String properEmail = resolveEmail(emp, everfiUser);

            everfiUserClient.updateUser(EverfiUpdateUserCommand.builder()
                    .uuid(everfiUser.getUuid())
                    .employeeId(emp.getEmployeeId())
                    .firstName(emp.getFirstName())
                    .lastName(emp.getLastName())
                    .email(properEmail)
                    .ssoId("")
                    .categoryLabels(getOrCreateEmpCategoryLabels(emp, everfiUser))
                    .active(emp.isActive())
                    .build());

        } catch (Exception e) {
            logger.warn("error " + e);
        }
    }

    /**
     * Determines what email to attach to an EverfiUser.
     * We prefer to use the employee's senate email whenever possible, but some employees have requested
     * to use their personal email addresses, in which case we should not override it.
     *
     * @return a valid email address or null if neither email is valid.
     */
    public String resolveEmail(Employee emp, EverfiUser everfiUser) {
        boolean employeeEmailUsable = emp.isActive() && EmailValidator.getInstance().isValid(emp.getEmail());
        boolean everfiEmailUsable = EmailValidator.getInstance().isValid(everfiUser.getEmail());

        if (!everfiEmailUsable) {
            return employeeEmailUsable ? emp.getEmail() : null;
        }

        if (looksLikePersonalEmailOverride(everfiUser.getEmail())) {
            return everfiUser.getEmail();
        }

        return employeeEmailUsable ? emp.getEmail() : everfiUser.getEmail();
    }

    /**
     * Some users have requested to use their personal email in Everfi instead of their senate email.
     * This is a simple attempt at recognizing those emails.
     *
     * @return true if {@code email} may be a personal email address.
     */
    private boolean looksLikePersonalEmailOverride(String email) {
        EmailValidator validator = EmailValidator.getInstance();
        return validator.isValid(email) && !StringUtils.containsIgnoreCase(email, "@nysenate.gov");
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
        EverfiCategoryLabel label = user == null ? null : categoryService.findUploadListLabel(user.getCategoryLabels());
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
