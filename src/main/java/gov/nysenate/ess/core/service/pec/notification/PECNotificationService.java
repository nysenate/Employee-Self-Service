package gov.nysenate.ess.core.service.pec.notification;

import gov.nysenate.ess.core.dao.pec.assignment.PersonnelTaskAssignmentDao;
import gov.nysenate.ess.core.dao.pec.notification.PECNotificationDao;
import gov.nysenate.ess.core.model.pec.PersonnelTask;
import gov.nysenate.ess.core.model.pec.PersonnelTaskAssignment;
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
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static java.nio.file.StandardOpenOption.*;

@Service
public class PECNotificationService {
    private static final Logger logger = LoggerFactory.getLogger(PECNotificationService.class);

    private final PersonnelTaskAssignmentDao assignmentDao;
    private final SendMailService sendMailService;
    private final EmployeeInfoService employeeInfoService;
    private final PECNotificationDao pecNotificationDao;
    private final Map<Integer, PersonnelTask> activeTaskMap = new HashMap<>();
    private final List<String> reportEmails;
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

    // TODO: test and remove?
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
            List<PersonnelTaskAssignment> incompleteTaskAssignments = getIncompleteTaskAssignments(employee);
            if (incompleteTaskAssignments.isEmpty()) {
                continue;
            }
            // Continue processing employee if they have outstanding assignments and a valid email
            if (employee.getEmail() == null || employee.getEmail().isEmpty()) {
                employeesWithMissingEmails.add(employee);
                logger.warn("Employee %s #%d is missing an email! No notification will be sent to them."
                        .formatted(employee.getFullName(), employee.getEmployeeId()));
            } else {
                Map<PersonnelTask, LocalDate> taskMap = incompleteTaskAssignments.stream()
                        .collect(Collectors.toMap(
                                assignment -> activeTaskMap.get(assignment.getTaskId()),
                                assignment -> assignment.getDueDate().toLocalDate()));
                emailsToSend.add(new EmployeeEmail(employee, EmailType.REMINDER, taskMap, pecTestMode));
            }
        }
        if (!employeesWithMissingEmails.isEmpty() && sendAdminEmails) {
            String html = PecEmailUtils.getMissingEmailHtml(employeesWithMissingEmails);
            for (String adminEmail : reportEmails) {
                sendEmail(new ReportEmail(adminEmail, EmailType.REPORT_MISSING, html));
            }
        }
        return emailsToSend;
    }

    /**
     * The actual scheduled process. Will fetch and send out emails.
     */
    public void runPECNotificationProcess() {
        logger.info("Starting PEC Notification Process");
        resetTestModeCounter();
        getScheduledEmails(true).forEach(this::sendEmail);
        logger.info("Completed PEC Notification Process");
    }

    public Optional<EmployeeEmail> getInviteEmail(int empId, PersonnelTask task, LocalDateTime dueDate) {
        Employee employee = employeeInfoService.getEmployee(empId);
        boolean wasNotifSent = pecNotificationDao.wasNotificationSent(empId, task.getTaskId());

        // TODO: IsNotifiable should be handled generally
        if (!task.isActive() || wasNotifSent || employee.isSenator() || !task.isNotifiable()) {
            return Optional.empty();
        }

        Map<PersonnelTask, LocalDate> taskMap = new HashMap<>();
        taskMap.put(task, dueDate == null ? null : dueDate.toLocalDate());
        return Optional.of(new EmployeeEmail(employee, EmailType.INVITE, taskMap, pecTestMode));
    }

    public void sendInviteEmail(EmployeeEmail email) {
        sendEmail(email);
        if (!pecTestMode) {
            pecNotificationDao.markNotificationSent(email.getEmployee().getEmployeeId(),
                    email.tasks().get(0).getTaskId());
        }
    }

    public void sendCompletionEmail(int empId, PersonnelTask task) {
        if (!task.isNotifiable()) {
            return;
        }
        Employee employee = employeeInfoService.getEmployee(empId);
        var emailInfo = new EmployeeEmail(employee, EmailType.COMPLETION, task);
        sendEmail(emailInfo);
    }

    /**
     * Sends emails. Limited by test mode and PEC notifs enabled.
     */
    public void sendEmail(NotificationEmail emailInfo) {
        if (!allPecNotifsEnabled) {
            return;
        }
        if (emailInfo.isLimited() && emailLimit < emailCount) {
            return;
        }
        else {
            emailCount++;
        }
        String address = emailInfo.sendTo().trim();
        // Keeps spacing consistent and positive.
        String spaces = " ".repeat(Math.max(28 - address.length(), 1));
        String logMessage = "Recipient: %sSubject: %s\n".formatted(address + spaces, emailInfo.subject());
        if (pecTestMode) {
            address = reportEmails.get(0);
        }
        MimeMessage message = sendMailService.newHtmlMessage(address, emailInfo.subject(), emailInfo.html);
        try {
            sendMailService.send(message);
            Files.writeString(emailLogPath, logMessage, CREATE, WRITE, APPEND);
        } catch (Exception e) {
            logger.error("There was an error trying to send the PEC notification email ", e);
        }
    }

    // TODO: BaseGroupTaskAssigner should really just have an EmployeeInfoService,
    //  getting this information isn't this class' responsibility.
    public LocalDate getContinuousServiceDate(int empId) {
        return employeeInfoService.getEmployeesMostRecentContinuousServiceDate(empId);
    }

    private List<PersonnelTaskAssignment> getIncompleteTaskAssignments(Employee employee) {
        return assignmentDao.getAssignmentsForEmp(employee.getEmployeeId()).stream()
                .filter(assignment -> assignment.isActive() && !assignment.isCompleted())
                .filter(assignment -> activeTaskMap.containsKey(assignment.getTaskId()))
                .collect(Collectors.toList());
    }

    public Map<Integer, PersonnelTask> getActiveTaskMap() {
        return activeTaskMap;
    }
}
