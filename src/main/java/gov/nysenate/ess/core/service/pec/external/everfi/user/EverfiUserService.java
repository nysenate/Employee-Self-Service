package gov.nysenate.ess.core.service.pec.external.everfi.user;

import gov.nysenate.ess.core.dao.pec.everfi.EverfiUserDao;
import gov.nysenate.ess.core.dao.personnel.EmployeeDao;
import gov.nysenate.ess.core.model.personnel.EmployeeNotFoundEx;
import gov.nysenate.ess.core.service.mail.SendMailService;
import gov.nysenate.ess.core.service.pec.external.everfi.EverfiApiClient;
import gov.nysenate.ess.core.service.pec.external.everfi.EverfiRecordService;
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
    private ArrayList<String> badUUIDs = new ArrayList<>();

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
        this.badUUIDs.clear();

        while (request != null) {
            //Contact everfi api
            everfiUsers = request.getUsers();

            //Process records / insert into db
            handleUserRecords(everfiUsers);

            //Get next batch of records
            request = request.next();
        }
        logger.info("Handled Everfi user records");
        String everfiIDWarning = "There are " + this.badUUIDs.size() + " UUID's that have bad data. The list of UUID's with bad data in the EMPID or EMAIL fields are: "
                + this.badUUIDs.toString();
        logger.warn(everfiIDWarning);
        MimeMessage message = sendMailService.newHtmlMessage(mailToAddress.trim(), "Bad data in Everfi Users", everfiIDWarning);
        sendMailService.send(message);
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
                    addUUIDtoBadList(UUID);
                    logger.warn("Everfi user with UUID " + UUID + " empid was improperly retrieved");
                }
            }
            catch (DuplicateKeyException e) {
                //Do nothing, it means we already have the user stored in the DB
            }
            catch (EmployeeNotFoundEx e) {
                addUUIDtoBadList(UUID);
                logger.warn("Everfi user with UUID " + UUID + " cannot be matched");
            }
        }
    }

    /**
     * Gets the employee object and then their id base off of the everfi email or emp id on file
     */
    private int getEmployeeId(EverfiUser everfiUser) throws EmployeeNotFoundEx {
        int empid = 99999;
        Integer everfiUserEmpID = (Integer) everfiUser.getEmployeeId();

        if ( !isNullorZero(everfiUserEmpID) ) {
            try {
                empid = employeeDao.getEmployeeById(everfiUserEmpID).getEmployeeId();
            } catch (Exception e) {
                addUUIDtoBadList(everfiUser.getUuid());
            }
        }
        else {
            addUUIDtoBadList(everfiUser.getUuid());
        }

        if (everfiUser.getEmail() != null && !everfiUser.getEmail().isEmpty()) {

            try {
                empid = employeeDao.getEmployeeByEmail(everfiUser.getEmail()).getEmployeeId();
            } catch (Exception e) {
                addUUIDtoBadList(everfiUser.getUuid());
            }
        } else {
            addUUIDtoBadList(everfiUser.getUuid());
            throw new EmployeeNotFoundEx("Everfi user record cannot be matched" + everfiUser.toString());
        }

        return empid;
    }

    private void addUUIDtoBadList(String UUID) {
        //There is a potential for the same UUID to get here multiple times. We only want it reported once
        if (!this.badUUIDs.contains(UUID)) {
            this.badUUIDs.add(UUID);
        }
    }

    public static boolean isNullorZero(Integer i){
        return 0 == ( i == null ? 0 : i);
    }

}
