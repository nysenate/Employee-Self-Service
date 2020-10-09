package gov.nysenate.ess.core.service.pec.external.everfi.user;

import gov.nysenate.ess.core.dao.pec.everfi.EverfiUserDao;
import gov.nysenate.ess.core.dao.personnel.EmployeeDao;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.model.personnel.EmployeeNotFoundEx;
import gov.nysenate.ess.core.service.mail.SendMailService;
import gov.nysenate.ess.core.service.pec.external.everfi.EverfiApiClient;
import gov.nysenate.ess.core.service.pec.external.everfi.EverfiRecordService;
import gov.nysenate.ess.core.service.pec.external.everfi.user.update.EverfiUpdateUserRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class EverfiUserService {

    private EverfiApiClient everfiApiClient;
    private EmployeeDao employeeDao;
    private EverfiUserDao everfiUserDao;
    private EverfiRecordService everfiRecordService;
    private SendMailService sendMailService;
    private static final Logger logger = LoggerFactory.getLogger(EverfiUserService.class);
    private ArrayList<EverfiUser> badEmpIDEverfiUsers = new ArrayList<>();
    private ArrayList<EverfiUser> badEmailEverfiUsers = new ArrayList<>();
    ArrayList<String> manualReviewUUIDs = new ArrayList<>();

    @Value("${mail.smtp.from}")
    private String mailFromAddress;

    @Value("${report.email}")
    private String mailToAddress;



    @Autowired
    public EverfiUserService(EverfiApiClient everfiApiClient, EmployeeDao employeeDao, EverfiUserDao everfiUserDao,
                             EverfiRecordService everfiRecordService, SendMailService sendMailService) {
        this.everfiApiClient = everfiApiClient;
        this.employeeDao = employeeDao;
        this.everfiUserDao = everfiUserDao;
        this.everfiRecordService = everfiRecordService;
        this.sendMailService = sendMailService;
    }

    /**
     * Entry point to get all Everfi UUID's and EmpIds in the database for future use
     * @throws IOException
     */
    public void getEverfiUserIds() throws IOException {
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
        logger.info("Handled Everfi user records");
        String everfiIDWarning = "There are " + this.badEmpIDEverfiUsers.size() +
                " UUID's that have bad EMP IDs.\n" + this.badEmpIDEverfiUsers.toString() + "\n"
                + "There are " + this.badEmailEverfiUsers.size() + " UUID's that have bad emails. \n" + this.badEmailEverfiUsers.toString() + "\n";
        logger.warn(everfiIDWarning);
        MimeMessage message = sendMailService.newHtmlMessage(mailToAddress.trim(), "Bad data in Everfi Users", everfiIDWarning);
        sendMailService.send(message);
//        handleEverfiUsersWithBadEmail();
//        handleEverfiUsersWithBadEmpID();
//        message = sendMailService.newHtmlMessage(mailToAddress.trim(), "Bad data in Everfi Users that REQUIRE manual review", this.manualReviewUUIDs.toString());
//        sendMailService.send(message);
    }

    /**
     * This method finds and inserts an everfi user UUID with a Senate Emp ID into the database
     * @param everfiUsers
     */
    private void handleUserRecords(List<EverfiUser> everfiUsers) {

        for (EverfiUser everfiUser : everfiUsers) {
            String UUID = everfiUser.getUuid();
            try {
                Integer empid = getEmployeeId(everfiUser);
                if (empid.intValue() != 99999) {
                    everfiUserDao.insertEverfiUserIDs(UUID,empid);
                }
                else {
                    logger.debug("Everfi user with UUID " + UUID + " empid was improperly retrieved");
                }
            }
            catch (DuplicateKeyException e) {
                //Do nothing, it means we already have the user stored in the DB
            }
            catch (EmployeeNotFoundEx e) {
                logger.debug("Everfi user with UUID " + UUID + " cannot be matched");
            }
        }
    }

    /**
     * Gets the employee object and then their id base off of the everfi email or emp id on file
     */
    private int getEmployeeId(EverfiUser everfiUser) throws EmployeeNotFoundEx {
        int empid = 99999;
        //We have to test both cases because they could be different / then we must correct that
        int empidByID = getEmpIDByID(everfiUser);
        int empidByEmail = getEmpIDByEmail(everfiUser);

        if (empidByID != empidByEmail) {
            //WE NEED MANUAL REVIEW ON THIS CASE
            throw new EmployeeNotFoundEx("Everfi user record cannot be matched" + everfiUser.toString());
        }
        if (empidByID == empidByEmail) {
            return empidByID;
        }

        return empid;
    }

    private int getEmpIDByID(EverfiUser everfiUser) {
        int empid = 99999;
        Integer everfiUserEmpID = (Integer) everfiUser.getEmployeeId();

        if ( !isNullorZero(everfiUserEmpID) ) {
            try {
                empid = employeeDao.getEmployeeById(everfiUserEmpID).getEmployeeId();
            } catch (Exception e) {
                addBadEverfiUser(everfiUser, this.badEmpIDEverfiUsers);
            }
        }
        else {
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

    public void handleEverfiUsersWithBadEmpID() {
        for (EverfiUser everfiUser : this.badEmpIDEverfiUsers ) {
            updateEverfiUserWithEmpData(getEmpIDByEmail(everfiUser), everfiUser);
        }
    }

    public void handleEverfiUsersWithBadEmail() {
        for (EverfiUser everfiUser : this.badEmailEverfiUsers ) {
            updateEverfiUserWithEmpData(getEmpIDByID(everfiUser), everfiUser);
        }
    }

    private void updateEverfiUserWithEmpData(int empID, EverfiUser everfiUser) {
        if (empID != 99999) {
            try {
                Employee emp = employeeDao.getEmployeeById(empID);
                EverfiUpdateUserRequest updateUserRequest =
                        new EverfiUpdateUserRequest(everfiApiClient,everfiUser.getUuid(),emp.getEmployeeId(),
                                emp.getFirstName(), emp.getLastName(),emp.getEmail(),null,
                                everfiUser.getUserCategoryLabels(), emp.isActive());
                updateUserRequest.updateUser();

            }
            catch (Exception e) {
                this.manualReviewUUIDs.add(everfiUser.getUuid());
                logger.warn("error" + e);
            }
        }
    }

    private void addBadEverfiUser(EverfiUser everfiUser, List<EverfiUser> badList) {
        boolean recorded = false;
        for (EverfiUser badDataUser: badList) {
            if (badDataUser.getUuid().equals(everfiUser.getUuid())) {
                recorded = true;
            }
        }
        if (!recorded) {
            badList.add(everfiUser);
        }
    }

    public static boolean isNullorZero(Integer i){
        return 0 == ( i == null ? 0 : i);
    }

}
