package gov.nysenate.ess.core.service.pec.notification;

import gov.nysenate.ess.core.dao.pec.assignment.PersonnelTaskAssignmentDao;
import gov.nysenate.ess.core.dao.pec.notification.PECNotificationDao;
import gov.nysenate.ess.core.model.pec.PersonnelTask;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.service.mail.SendMailService;
import gov.nysenate.ess.core.service.pec.task.PersonnelTaskService;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

import static java.nio.file.StandardOpenOption.*;

@Service
public class PECNotificationService {
    private static final Logger logger = LoggerFactory.getLogger(PECNotificationService.class);
    private static final String standardHtml = "<b>%s, our records indicate you have outstanding tasks to complete for Personnel.</b><br>" +
            "You can find instructions to complete them by logging into ESS, " +
            "then clicking the My Info tab and clicking on the To Do List. " +
            "Or, go to this link <a href=\"%s\">HERE</a><br><br>" +
            "<b>You must complete the following tasks: </b><br><br>\n\n";

    private final PersonnelTaskAssignmentDao assignmentDao;
    private final SendMailService sendMailService;
    private final EmployeeInfoService employeeInfoService;
    private final PECNotificationDao pecNotificationDao;
    private final Map<Integer, PersonnelTask> activeTaskMap = new HashMap<>();
    private final List<String> reportEmails;
    private final String instructionURL;
    private final boolean pecTestMode;
    private final double emailLimit;
    private final Path emailLogPath;
    private double emailCount = 0;

    @Value("${scheduler.pec.notifs.enabled:false}")
    private boolean pecNotifsEnabled;
    @Value("${all.pec.notifs.enabled:false}")
    private boolean allPecNotifsEnabled;

    public PECNotificationService(PersonnelTaskService taskService,
                                  PersonnelTaskAssignmentDao assignmentDao, SendMailService sendMailService,
                                  EmployeeInfoService employeeInfoService,
                                  PECNotificationDao pecNotificationDao,
                                  @Value("${report.email}") String reportEmailList,
                                  @Value("${domain.url}") String domainURL,
                                  @Value("${pec.test.mode:true}") boolean pecTestMode,
                                  @Value("${data.dir}") String dataDir) {
        this.assignmentDao = assignmentDao;
        this.sendMailService = sendMailService;
        this.employeeInfoService = employeeInfoService;
        this.pecNotificationDao = pecNotificationDao;
        for (PersonnelTask task : taskService.getPersonnelTasks(true)) {
            activeTaskMap.put(task.getTaskId(), task);
        }
        this.reportEmails = List.of(reportEmailList.replaceAll(" ", "").split(","));
        this.instructionURL = domainURL + "/myinfo/personnel/todo";
        this.pecTestMode= pecTestMode;
        this.emailLimit = pecTestMode ? 5 : Double.POSITIVE_INFINITY;
        this.emailLogPath = Path.of(dataDir, "emailLog.txt");
    }

    @Scheduled(cron = "${scheduler.pec.notifs.cron}")
    public void runUpdateMethods() {
        if (pecNotifsEnabled) {
            runPECNotificationProcess();
        }
    }

    public void resetTestModeCounter() {
        emailCount = 0;
    }

    /**
     * Fetches data on emails that would be sent right now, if the relevant cron ran.
     * @param sendAdminEmails whether to send emails about employees with missing emails.
     * @return List of email information.
     */
    public List<EmployeeEmail> getScheduledEmails(boolean sendAdminEmails) {
        // Document all employees with missing info to report to admins
        List<Employee> employeesWithMissingEmails = new ArrayList<>();
        List<EmployeeEmail> emailsToSend = new ArrayList<>();

        for (Employee employee : employeeInfoService.getAllEmployees(true)) {
            List<PersonnelTask> incompleteTasks = getIncompleteTasks(employee);
            if (incompleteTasks.isEmpty()) {
                continue;
            }
            // Continue processing employee if they have outstanding assignments and a valid email
            if (employee.getEmail() == null || employee.getEmail().isEmpty()) {
                employeesWithMissingEmails.add(employee);
                logger.warn("Employee %s #%d is missing an email! No notification will be sent to them."
                        .formatted(employee.getFullName(), employee.getEmployeeId()));
            } else {
                emailsToSend.add(new EmployeeEmail(employee, EmailType.REMINDER, incompleteTasks));
            }
        }
        if (!employeesWithMissingEmails.isEmpty() && sendAdminEmails) {
            String html = getMissingEmailHtml(employeesWithMissingEmails);
            for (String adminEmail : reportEmails) {
                sendEmail(new NotificationEmail(adminEmail, EmailType.REPORT_MISSING), html);
            }
        }
        return emailsToSend;
    }

    private static String getMissingEmailHtml(List<Employee> employeesWithMissingEmails) {
        return employeesWithMissingEmails.stream()
                .map(employee -> " NAME: " + employee.getFullName()+ " EMPID: " + employee.getEmployeeId() + "<br>\n")
                .collect(Collectors.joining());
    }

    /**
     * The actual scheduled process. Will fetch and send out emails.
     */
    public void runPECNotificationProcess() {
        logger.info("Starting PEC Notification Process");
        resetTestModeCounter();
        for (var emailInfo : getScheduledEmails(true)) {
            if (emailLimit >= emailCount) {
                sendEmail(emailInfo, dueDateInformationHtml(emailInfo));
                emailCount++;
            }
        }
        logger.info("Completed PEC Notification Process");
    }

    public void sendInviteEmail(int empId, PersonnelTask task) {
        Employee employee = employeeInfoService.getEmployee(empId);
        boolean wasNotifSent = pecNotificationDao.wasNotificationSent(empId, task.getTaskId());

        if (!task.isActive() || wasNotifSent || employee.isSenator() || !task.isNotifiable()) {
            return;
        }

        if (emailLimit >= emailCount) {
            var emailInfo = new EmployeeEmail(employee, EmailType.INVITE, task);
            sendEmail(emailInfo, dueDateInformationHtml(emailInfo));
            emailCount++;
        }
        if (!pecTestMode) {
            // TODO: skip this if we're just generating an email to look at
            pecNotificationDao.markNotificationSent(empId, task.getTaskId());
        }
    }

    public void sendCompletionEmail(int empId, PersonnelTask task) {
        if (!task.isNotifiable()) {
            return;
        }
        Employee employee = employeeInfoService.getEmployee(empId);;
        var emailInfo = new EmployeeEmail(employee, EmailType.COMPLETION, task);
        String html = "Our records have been updated to indicate you have completed " + task.getTitle();
        sendEmail(emailInfo, html);
    }

    /**
     * Sends emails. Limited by test mode and PEC notifs enabled.
     */
    public void sendEmail(NotificationEmail emailInfo, String html) {
        if (!allPecNotifsEnabled) {
            return;
        }
        String address = emailInfo.sendTo().trim();
        // Keeps spacing consistent and positive.
        String spaces = " ".repeat(Math.max(28 - address.length(), 1));
        String logMessage = "Recipient: %sSubject: %s\n".formatted(address + spaces, emailInfo.subject());
        if (pecTestMode) {
            address = reportEmails.get(0);
        }
        MimeMessage message = sendMailService.newHtmlMessage(address, emailInfo.subject(), html);
        try {
            sendMailService.send(message);
            Files.writeString(emailLogPath, logMessage, CREATE, WRITE, APPEND);
        } catch (Exception e) {
            logger.error("There was an error trying to send the PEC notification email ", e);
        }
    }

    private List<PersonnelTask> getIncompleteTasks(Employee employee) {
        return assignmentDao.getAssignmentsForEmp(employee.getEmployeeId()).stream()
                .filter(assignment -> assignment.isActive() && !assignment.isCompleted())
                .map(assignment -> activeTaskMap.get(assignment.getTaskId()))
                .filter(Objects::nonNull).collect(Collectors.toList());
    }

    private String dueDateInformationHtml(EmployeeEmail emailInfo) {
        Employee employee = emailInfo.getEmployee();
        LocalDate activeServiceDate =
                employeeInfoService.getEmployeesMostRecentContinuousServiceDate(employee.getEmployeeId());
        String html = standardHtml.formatted(employee.getFullName(), instructionURL) +
                emailInfo.tasks().stream().map(task -> getHtml(task, activeServiceDate)).collect(Collectors.joining());
        if (pecTestMode) {
            html += "<br> Employee ID: #" + employee.getEmployeeId() + "<br> Email: " + employee.getEmail();
        }
        return html;
    }

    public Map<Integer, PersonnelTask> getActiveTaskMap() {
        return activeTaskMap;
    }

    // TODO: the functions below should ideally be in EmailType

    public static String getHtml(PersonnelTask task, LocalDate activeServiceDate) {
        String html = "<li>" + task.getTitle() + "</li>\n";
        html += (switch (task.getTaskType()) {
            case DOCUMENT_ACKNOWLEDGMENT, VIDEO_CODE_ENTRY, ETHICS_COURSE, EVERFI_COURSE -> "";
            case MOODLE_COURSE -> "You have 30 days to complete this assignment from your hiring date." +
                    " It is due by " + getDueDate(activeServiceDate, 30) + "<br>";
            case ETHICS_LIVE_COURSE -> getEthicsLiveDetails(activeServiceDate);
        });
        return html + "<br>";
    }

    public static LocalDate getDueDate(LocalDate continuousServiceDate, int daysToAdd) {
        Date startDate = Date.from(continuousServiceDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Calendar calendar = new GregorianCalendar(/* remember about timezone! */);
        calendar.setTime(startDate);
        calendar.add(Calendar.DATE, daysToAdd);
        return calendar.getTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    private static String getEthicsLiveDetails(LocalDate activeServiceDate) {
        String ethicsLiveStr;
        // Checks if they are an already existing employee.
        if (activeServiceDate.isBefore(LocalDate.now().minus(Period.ofDays(90)))) {
            ethicsLiveStr = "You have until the end of the current calendar year to complete this course.";
        }
        // Only applies if they are a new hire.
        else {
            ethicsLiveStr = "You have 90 days to complete this assignment from your hiring date: " +
                    activeServiceDate + ". It is due by: " + getDueDate(activeServiceDate, 90) + ".";
        }
        return ethicsLiveStr + " These live sessions run once a month.<br>";
    }
}
