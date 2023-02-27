package gov.nysenate.ess.core.service.pec.task;

import gov.nysenate.ess.core.dao.pec.task.PersonnelTaskDao;
import gov.nysenate.ess.core.dao.pec.task.detail.EthicsLiveCourseTaskDetailDao;
import gov.nysenate.ess.core.model.pec.PersonnelTask;
import gov.nysenate.ess.core.model.pec.PersonnelTaskType;
import gov.nysenate.ess.core.service.pec.notification.PECNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class PersonnelCodeGenerationService {
    PECNotificationService pecNotificationService;

    PersonnelTaskDao personnelTaskDao;

    @Value("${pec.ethics-code.autogen:false}")
    private boolean pecEthicsCodeAutogen;

    private List<String> pecCodeAdminEmails;

    private Map<Integer, PersonnelTask> activeTaskMap;

    List<String> numberList = Arrays.asList(IntStream.rangeClosed('0', '9')
            .mapToObj(c -> (char) c+",").collect(Collectors.joining()).split(","));

    List<String> characterList = Arrays.asList(IntStream.concat(IntStream.rangeClosed('P', 'Z'),
                    IntStream.concat( IntStream.rangeClosed('A', 'H'), IntStream.rangeClosed('J', 'N')))
            .mapToObj(c -> (char) c+",").collect(Collectors.joining()).split(","));

    List<String> DecisionList = Arrays.asList(IntStream.rangeClosed('1', '2')
            .mapToObj(c -> (char) c+",").collect(Collectors.joining()).split(","));

    @Autowired
    public PersonnelCodeGenerationService(PECNotificationService pecNotificationService,
                                          PersonnelTaskDao personnelTaskDao,
                                          @Value("${pec.code.admin.emails}") String pecCodeAdminEmails) {
        this.pecNotificationService = pecNotificationService;
        this.personnelTaskDao = personnelTaskDao;
        this.pecCodeAdminEmails = Arrays.asList(pecCodeAdminEmails.replaceAll(" ", "").split(","));
        this.activeTaskMap = pecNotificationService.getActiveTaskMap();
    }

    @Scheduled(cron = "${scheduler.ethics.code.autogen.cron}")
    public void runUpdateMethods() {
        if (pecEthicsCodeAutogen) {
            handleCodeChangesForEthicsLiveCourses();
        }
    }

    public void handleCodeChangesForEthicsLiveCourses() {
        //Get all ACTIVE ethics live courses. At most should be 2 active at any time
        List<PersonnelTask> ethicsLiveTasks = getEthicsLiveCourses();

        boolean isFirstQuarter = isFirstQuarterOfTheYear();

        if (isFirstQuarter) {
            //all tasks get the same codes
            String code1 = createCode();
            String code2 = createCode();

            for (PersonnelTask task: ethicsLiveTasks) {
                //Update codes in DB
                Integer ethicsCodeID = personnelTaskDao.getEthicsCodeId(task.getTaskId());
                personnelTaskDao.updateEthicsCode(code1,ethicsCodeID,1);
                personnelTaskDao.updateEthicsCode(code2,ethicsCodeID,2);
                sendEmailsToAdmins(task, code1, code2);
            }
        }
        else {
            //different codes per task
            for (PersonnelTask task: ethicsLiveTasks) {

                String code1 = createCode();
                String code2 = createCode();
                //Update codes in DB
                Integer ethicsCodeID = personnelTaskDao.getEthicsCodeId(task.getTaskId());
                personnelTaskDao.updateEthicsCode(code1,ethicsCodeID,1);
                personnelTaskDao.updateEthicsCode(code2,ethicsCodeID,2);

                sendEmailsToAdmins(task, code1, code2);
            }
        }
    }

    public void sendEmailsToAdmins(PersonnelTask task, String code1, String code2) {
        String subject = "New Codes for Ethics Live Course: " + task.getTitle();
        String html = "The new codes are <br> CODE 1: " + code1 + "<br>" + "CODE 2: " + code2;
        for (String email : pecCodeAdminEmails) {
            pecNotificationService.sendEmail(email,subject,html);
        }
    }

    //Returns a 6 digit code coprised of numbers and letters
    public String createCode() {
        String code = "";
        for (int i=0; i<6; i++) {
            int decision = Integer.parseInt( decideNumberOrCharacter() );
            if (decision == 1) {
                code = code + selectCharacterFromList();
            }
            else {
                code = code + selectNumberFromList();
            }
        }
        return code;
    }

    private List<PersonnelTask> getEthicsLiveCourses() {
        ArrayList<PersonnelTask> ethicsLiveTaks = new ArrayList<>();
        for (PersonnelTask task : this.activeTaskMap.values()) {
            if (task.getTaskType() == PersonnelTaskType.ETHICS_LIVE_COURSE && task.isActive()) {
                ethicsLiveTaks.add(task);
            }
        }
        return ethicsLiveTaks;
    }

    private boolean isFirstQuarterOfTheYear() {
        //Find out what today is
        LocalDate now = LocalDate.now();

        //Date range of the first quarter
        LocalDate january = LocalDate.of(now.getYear(),1,1);
        LocalDate march = LocalDate.of(now.getYear(),3,31);

        if (now.isAfter(january) && now.isBefore(march)) {
            return true;
        }
        return false;
    }

    private String selectNumberFromList() {
        Random rand = new Random();
        return numberList.get(rand.nextInt(numberList.size()));
    }

    private String selectCharacterFromList() {
        Random rand = new Random();
        return characterList.get(rand.nextInt(characterList.size()));
    }

    private String decideNumberOrCharacter() {
        Random rand = new Random();
        return DecisionList.get(rand.nextInt(DecisionList.size()));
    }

}
