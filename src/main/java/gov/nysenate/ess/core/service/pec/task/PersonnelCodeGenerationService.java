package gov.nysenate.ess.core.service.pec.task;

import gov.nysenate.ess.core.dao.pec.task.PersonnelTaskDao;
import gov.nysenate.ess.core.model.pec.PersonnelTask;
import gov.nysenate.ess.core.model.pec.PersonnelTaskType;
import gov.nysenate.ess.core.service.pec.notification.PECNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class PersonnelCodeGenerationService {
    private static final List<Character> charList = new ArrayList<>();
    static {
        for (char c = 'A'; c <= 'Z'; c++) {
            if (c != 'I' && c != 'O') {
                charList.add(c);
            }
        }
        for (char c = '0'; c <= '9'; c++) {
            charList.add(c);
        }
    }

    private final PECNotificationService pecNotificationService;
    private final PersonnelTaskDao personnelTaskDao;
    private final List<String> pecCodeAdminEmails;

    @Value("${pec.ethics-code.autogen:false}")
    private boolean pecEthicsCodeAutogen;

    @Autowired
    public PersonnelCodeGenerationService(PECNotificationService pecNotificationService,
                                          PersonnelTaskDao personnelTaskDao,
                                          @Value("${pec.code.admin.emails}") String pecCodeAdminEmails) {
        this.pecNotificationService = pecNotificationService;
        this.personnelTaskDao = personnelTaskDao;
        this.pecCodeAdminEmails = Arrays.asList(pecCodeAdminEmails.replaceAll(" ", "").split(","));
    }

    @Scheduled(cron = "${scheduler.ethics.code.autogen.cron}")
    public void runUpdateMethods() {
        if (pecEthicsCodeAutogen) {
            handleCodeChangesForEthicsLiveCourses();
        }
    }

    public void handleCodeChangesForEthicsLiveCourses() {
        //Get all ACTIVE ethics live courses. At most should be 2 active at any time.
        List<PersonnelTask> ethicsLiveTasks = getEthicsLiveCourses();
        boolean isFirstQuarter = isFirstQuarterOfTheYear();
        String code1 = createCode();
        String code2 = createCode();
        LocalDateTime startDate=generateEthicsStartDate();
        LocalDateTime endDate=generateEthicsEndDate();



        for (PersonnelTask task : ethicsLiveTasks) {
            int ethicsCodeID = personnelTaskDao.getEthicsCodeId(task.getTaskId());
            personnelTaskDao.insertEthicsCode(ethicsCodeID, 1, "First Code", code1, startDate, endDate);
            personnelTaskDao.insertEthicsCode(ethicsCodeID, 2, "Second Code",code2, startDate, endDate);
            pecNotificationService.sendCodeEmail(pecCodeAdminEmails, code1, code2, task, startDate.toString(), endDate.toString());
            // Different codes per task
            if (!isFirstQuarter) {
                code1 = createCode();
                code2 = createCode();
            }
        }
    }

    private List<PersonnelTask> getEthicsLiveCourses() {
        return personnelTaskDao.getAllTasks().stream()
                .filter(task -> task.getTaskType() == PersonnelTaskType.ETHICS_LIVE_COURSE && task.isActive())
                .collect(Collectors.toList());
    }

    /**
     * @return a randomized 6-character string of letters and numbers.
     */
    public static String createCode() {
        return new Random().ints(6, 0, charList.size()).boxed()
                .map(i -> String.valueOf(charList.get(i))).collect(Collectors.joining());
    }

    public static LocalDateTime generateEthicsStartDate(){
        LocalDate now = LocalDate.now();
        LocalDateTime output = null;
        if(now.getDayOfMonth()>=28){
            output = LocalDateTime.of(now.getYear(), now.getMonthValue(), 28, 0, 0, 0);
            return output;
        }
        else if(now.getDayOfMonth()<28&&now.getMonthValue()>1){
            output = LocalDateTime.of(now.getYear(), now.getMonthValue()-1, 28, 0, 0 ,0);
            return output;
        }
        else{
            output = LocalDateTime.of(now.getYear()-1, 12, 28, 0, 0, 0);
            return output;
        }
    }

    public static LocalDateTime generateEthicsEndDate(){
        LocalDate now = LocalDate.now();
        LocalDateTime output = null;
        if(now.getDayOfMonth()>=28){
            output = LocalDateTime.of(now.getYear(), now.getMonthValue()+1, 28, 0,0,0);
            return output;
        }
        else if(now.getDayOfMonth()<28&&now.getMonthValue()>1){
            output = LocalDateTime.of(now.getYear(), now.getMonthValue(), 28, 0, 0, 0 );
            return output;
        }
        else{
            output = LocalDateTime.of(now.getYear()+1, 01, 28, 0, 0, 0);
            return output;
        }
    }

    private static boolean isFirstQuarterOfTheYear() {
        //Find out what today is
        LocalDate now = LocalDate.now();

        //Date range of the first quarter
        LocalDate january = LocalDate.of(now.getYear(),1,1);
        LocalDate march = LocalDate.of(now.getYear(),3,31);

        return now.isAfter(january) && now.isBefore(march);
    }
}
