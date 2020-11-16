package gov.nysenate.ess.core.service.pec.external.everfi.user;

import gov.nysenate.ess.core.dao.pec.everfi.EverfiUserDao;
import gov.nysenate.ess.core.dao.personnel.EmployeeDao;
import gov.nysenate.ess.core.model.pec.everfi.EverfiUserIDs;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.model.personnel.EmployeeNotFoundEx;
import gov.nysenate.ess.core.service.mail.SendMailService;
import gov.nysenate.ess.core.service.pec.external.everfi.EverfiApiClient;
import gov.nysenate.ess.core.service.pec.external.everfi.EverfiRecordService;
import gov.nysenate.ess.core.service.pec.external.everfi.category.EverfiCategoryLabel;
import gov.nysenate.ess.core.service.pec.external.everfi.category.EverfiCategoryService;
import gov.nysenate.ess.core.service.pec.external.everfi.user.add.EverfiAddUserRequest;
import gov.nysenate.ess.core.service.pec.external.everfi.user.update.EverfiUpdateUserRequest;
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
import java.util.*;

@Service
public class EverfiUserService {

    private EverfiApiClient everfiApiClient;
    private EmployeeDao employeeDao;
    private EverfiUserDao everfiUserDao;
    private EverfiRecordService everfiRecordService;
    private SendMailService sendMailService;
    private static final Logger logger = LoggerFactory.getLogger(EverfiUserService.class);
    private HashMap<String,EverfiUser> badEmpIDEverfiUsers = new HashMap<>();
    private HashMap<String,EverfiUser> badEmailEverfiUsers = new HashMap<>();
    private HashMap<String,EverfiUser> manualReviewUUIDs = new HashMap<>();
    private EverfiCategoryService categoryService;

    @Value("${mail.smtp.from}")
    private String mailFromAddress;

    @Value("${report.email}")
    private String mailToAddress;

    @Value("${scheduler.everfi.sync.enabled:false}")
    private boolean everfiSyncEnabled;


    @Autowired
    public EverfiUserService(EverfiApiClient everfiApiClient, EmployeeDao employeeDao, EverfiUserDao everfiUserDao,
                             EverfiRecordService everfiRecordService, SendMailService sendMailService,
                             EverfiCategoryService categoryService) {
        this.everfiApiClient = everfiApiClient;
        this.employeeDao = employeeDao;
        this.everfiUserDao = everfiUserDao;
        this.everfiRecordService = everfiRecordService;
        this.sendMailService = sendMailService;
        this.categoryService = categoryService;
    }

    @Scheduled(cron = "${scheduler.everfi.user.update.cron}")
    public void runUpdateMethods() throws IOException {
        if (everfiSyncEnabled) {
            //Add new employees to Everfi
            addEmployeesToEverfi(getNewEmployeesToAddToEverfi());
            //Update our db with their UUID from Everfi
            getEverfiUserIds();
        }
    }

    /**
     * Returns a list of all new employees that must be added to Everfi. Usually called thru cron
     * @return
     */
    public List<Employee> getNewEmployeesToAddToEverfi() {

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

            if (potentialEverfiUserID == null) {
                empsToAddToEverfi.add(completeNewEmp);
            }

        }

        return empsToAddToEverfi;

    }

    /**
     * Entry point to get all Everfi UUID's and EmpIds in the database for future use
     *
     * @throws IOException
     */
    public void getEverfiUserIds() throws IOException {
        if (everfiSyncEnabled) {
            EverfiUsersRequest request = new EverfiUsersRequest(everfiApiClient, 1, 1000);
            List<EverfiUser> everfiUsers;
            logger.info("Contacting Everfi for User records");
            this.badEmpIDEverfiUsers.clear();
            this.badEmailEverfiUsers.clear();
            this.manualReviewUUIDs.clear();

            while (request != null) {
                //Contact everfi api
                everfiUsers = request.getUsers();

                //Process records / insert into db
                handleUserRecords(everfiUsers);

                //Get next batch of records
                request = request.next();
            }
            ensureNonManualReviewIDs();
            logger.info("Handled Everfi user records");
            String everfiIDWarning = "There are " + this.badEmpIDEverfiUsers.size() +
                    " UUID's that have bad EMP IDs.\n" + this.badEmpIDEverfiUsers.values().toString() + "\n"
                    + "There are " + this.badEmailEverfiUsers.size() + " UUID's that have bad emails. \n" + this.badEmailEverfiUsers.values().toString() + "\n";
            logger.warn(everfiIDWarning);
            MimeMessage message = sendMailService.newHtmlMessage(mailToAddress.trim(), "Bad data in Everfi Users", everfiIDWarning);
            sendMailService.send(message);

            logger.info("Beginning bad data correction");
            handleEverfiUsersWithBadEmpID();
            handleEverfiUsersWithBadEmail();
            message = sendMailService.newHtmlMessage(mailToAddress.trim(),
                    "Bad data in Everfi Users that REQUIRE manual review", this.manualReviewUUIDs.values().toString());
            sendMailService.send(message);
            logger.info("Finished bad data correction. Check report email");
        }


    }

    /**
     * Ensure manual review ID's are not included with the automated data correction attempts
     */
    private void ensureNonManualReviewIDs() {
        this.badEmpIDEverfiUsers.entrySet()
                .removeIf( entry -> (this.manualReviewUUIDs.containsKey(entry.getKey())));

        this.badEmailEverfiUsers.entrySet()
                .removeIf( entry -> (this.manualReviewUUIDs.containsKey(entry.getKey())));
    }

    public void handleEverfiUsersWithBadEmpID() {
        for (EverfiUser everfiUser : this.badEmpIDEverfiUsers.values()) {
            updateEverfiUserWithEmpData(getEmpIDByEmail(everfiUser), everfiUser);
        }
    }

    public void handleEverfiUsersWithBadEmail() {
        for (EverfiUser everfiUser : this.badEmailEverfiUsers.values()) {
            updateEverfiUserWithEmpData(getEmpIDByID(everfiUser), everfiUser);
        }
    }

    /**
     * Adds the given Employees to Everfi.
     * These employees should not already exist in Everfi. There are separate methods for updating employee data.
     *
     * @param emps
     */
    public void addEmployeesToEverfi(List<Employee> emps) throws IOException {
        for (Employee emp : emps) {
            EverfiAddUserRequest addUserRequest = new EverfiAddUserRequest(
                    everfiApiClient, emp.getEmployeeId(), emp.getFirstName(), emp.getLastName(),
                    emp.getEmail(), getOrCreateEmpCategoryLabels(emp, null));
            EverfiUser newestEverfiUser = addUserRequest.addUser();
            if (newestEverfiUser != null) {
                everfiUserDao.insertEverfiUserIDs(newestEverfiUser.getUuid(), emp.getEmployeeId());
            }

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
            try {
                Integer empid = getEmployeeId(everfiUser);

                if (empid.intValue() != 99999) {
                    everfiUserDao.insertEverfiUserIDs(UUID, empid);
                } else {
                    logger.debug("Everfi user with UUID " + UUID + " empid was improperly retrieved");
                }
            } catch (DuplicateKeyException e) {
                //Do nothing, it means we already have the user stored in the DB
            } catch (EmployeeNotFoundEx e) {
                logger.debug("Everfi user with UUID " + UUID + " cannot be matched");
            }
        }
    }

    /**
     * Gets the employee object and then their id base off of the everfi email or emp id on file
     */
    private int getEmployeeId(EverfiUser everfiUser) throws EmployeeNotFoundEx {
        int empid = 99999;
        boolean successByEmail = false;
        boolean successByEmpID = false;

        //We have to test both cases because they could be different / then we must correct that
        int empidByID = getEmpIDByID(everfiUser);
        int empidByEmail = getEmpIDByEmail(everfiUser);

        if (empidByID != 99999) {
            successByEmpID = true;
        }
        if (empidByEmail != 99999) {
            successByEmail = true;
        }

        //Both bad needs manual review
        if (successByEmpID == false && successByEmail == false) {
            addBadEverfiUser(everfiUser, this.manualReviewUUIDs);
            throw new EmployeeNotFoundEx("Everfi user record cannot be matched" + everfiUser.toString());
        }

        //Both conflicting needs manual review
        if (empidByID != 99999 && (empidByID != empidByEmail) && empidByEmail != 99999) {
            addBadEverfiUser(everfiUser, this.manualReviewUUIDs);
            throw new EmployeeNotFoundEx("Everfi user record cannot be matched" + everfiUser.toString());
        }

        //They agree, Good Data, return either
        if (empidByID == empidByEmail) {
            return empidByID;
        }

        //Good empid but bad email
        if (successByEmpID == true && successByEmail == false) {
            return empidByID;
        }
        //Good email but bad empid
        else if (successByEmpID == false && successByEmail == true) {
            return empidByEmail;
        }

        return empid;
    }

    private void addBadEverfiUser(EverfiUser everfiUser, HashMap<String,EverfiUser> badList) {
        if (!badList.containsKey(everfiUser.getUuid())) {
            badList.put(everfiUser.getUuid(), everfiUser);
        }
    }

    private int getEmpIDByID(EverfiUser everfiUser) {
        int empid = 99999;
        Integer everfiUserEmpID = (Integer) everfiUser.getEmployeeId();

        if (!isNullorZero(everfiUserEmpID)) {
            try {
                empid = employeeDao.getEmployeeById(everfiUserEmpID).getEmployeeId();
            } catch (Exception e) {
                addBadEverfiUser(everfiUser, this.badEmpIDEverfiUsers);
            }
        } else {
            addBadEverfiUser(everfiUser, this.badEmpIDEverfiUsers);
        }
        return empid;
    }

    private int getEmpIDByEmail(EverfiUser everfiUser) {
        int empid = 99999;

        if (everfiUser.getEmail() != null && !everfiUser.getEmail().isEmpty()) {
            try {
                empid = employeeDao.getEmployeeByEmail(everfiUser.getEmail()).getEmployeeId();
            } catch (Exception e) {
                addBadEverfiUser(everfiUser, this.badEmailEverfiUsers);
            }
        } else {
            addBadEverfiUser(everfiUser, this.badEmailEverfiUsers);
        }
        return empid;
    }

    private void updateEverfiUserWithEmpData(int empID, EverfiUser everfiUser) {
        try {
            Employee emp = employeeDao.getEmployeeById(empID);
            EverfiUpdateUserRequest updateUserRequest =
                    new EverfiUpdateUserRequest(everfiApiClient, everfiUser.getUuid(), emp.getEmployeeId(),
                            emp.getFirstName(), emp.getLastName(), emp.getEmail(), "",
                            getOrCreateEmpCategoryLabels(emp, everfiUser), emp.isActive());
            updateUserRequest.updateUser();

        } catch (Exception e) {
            logger.warn("error " + e);
        }

    }

    private void updateAllEverfiUsers(List<EverfiUser> everfiUsers) {
        for (EverfiUser everfiUser : everfiUsers) {
            try {
                Integer empid = getEmployeeId(everfiUser);
                if (empid.intValue() != 99999) {
                    updateEverfiUserWithEmpData(empid, everfiUser);
                }
            }
            catch (Exception e) {
                logger.warn("error " + e);
            }
        }
    }

    private static boolean isNullorZero(Integer i) {
        return 0 == (i == null ? 0 : i);
    }

    private List<EverfiCategoryLabel> getOrCreateEmpCategoryLabels(Employee emp, EverfiUser user) throws IOException {
        List<EverfiCategoryLabel> labels = new ArrayList<>();
        labels.add(categoryService.getAttendLiveLabel(emp)); // This label is always "No" so it will never need to be created.
        labels.add(getOrCreateDepartmentLabel(emp));
        labels.add(categoryService.getRoleLabel(emp)); // All possible roles already exist.
//        labels.add(getOrCreateUploadListLabel(user));
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
        EverfiCategoryLabel label = user == null ? null : categoryService.getUserUploadListLabel(user);
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
