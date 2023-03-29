package gov.nysenate.ess.core.service.pec.notification;

import gov.nysenate.ess.core.dao.pec.assignment.PersonnelTaskAssignmentDao;
import gov.nysenate.ess.core.dao.pec.notification.PECNotificationDao;
import gov.nysenate.ess.core.model.pec.PersonnelTask;
import gov.nysenate.ess.core.model.pec.PersonnelTaskAssignment;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.service.mail.SendMailService;
import gov.nysenate.ess.core.service.pec.task.PersonnelTaskService;
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

import static gov.nysenate.ess.core.service.pec.notification.PecEmailType.*;
import static java.nio.file.StandardOpenOption.*;

@Service
public class PECNotificationService {
    private static final Logger logger = LoggerFactory.getLogger(PECNotificationService.class);

    private final PersonnelTaskAssignmentDao assignmentDao;
    private final SendMailService sendMailService;
    private final PecEmailUtils pecEmailUtils;
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
                                  PecEmailUtils pecEmailUtils,
                                  PECNotificationDao pecNotificationDao,
                                  @Value("${report.email}") String reportEmailList,
                                  @Value("${pec.test.mode:true}") boolean pecTestMode,
                                  @Value("${data.dir}") String dataDir) {
        this.assignmentDao = assignmentDao;
        this.sendMailService = sendMailService;
        this.pecEmailUtils = pecEmailUtils;
        this.pecNotificationDao = pecNotificationDao;
        for (PersonnelTask task : taskService.getPersonnelTasks(true)) {
            activeTaskMap.put(task.getTaskId(), task);
        }
        this.reportEmails = List.of(reportEmailList.replaceAll(" ", "").split(","));
        this.pecTestMode = pecTestMode;
        this.emailLimit = pecTestMode ? 5 : Double.POSITIVE_INFINITY;
        this.emailLogPath = Path.of(dataDir, "emailLog.txt");
    }

    @Scheduled(cron = "${scheduler.pec.notifs.cron}")
    public void runUpdateMethods() {
        if (pecNotifsEnabled) {
            runPECNotificationProcess();
        }
    }

    /**
     * Fetches data on emails that would be sent right now, if the relevant cron ran.
     * Notably does NOT include invite emails.
     * @param sendAdminEmails whether to send emails about employees with missing emails.
     * @return List of email information.
     */
    public List<EmployeeEmail> getScheduledEmails(boolean sendAdminEmails) {
        // Document all employees with missing info to report to admins
        List<Employee> employeesWithMissingEmails = new ArrayList<>();
        List<EmployeeEmail> emailsToSend = new ArrayList<>();
        for (Employee employee : pecEmailUtils.getActiveNonSenatorEmployees()) {
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
                Map<PersonnelTask, LocalDate> taskMap = new HashMap<>();
                for (var assignment : incompleteTaskAssignments) {
                    LocalDate date = assignment.getDueDate() == null ? null : assignment.getDueDate().toLocalDate();
                    taskMap.put(activeTaskMap.get(assignment.getTaskId()), date);
                }
                emailsToSend.add(new EmployeeEmail(employee, REMINDER, taskMap));
            }
        }
        if (!employeesWithMissingEmails.isEmpty() && sendAdminEmails) {
            String html = PecEmailUtils.getMissingEmailHtml(employeesWithMissingEmails);
            pecEmailUtils.getEmails(reportEmails, REPORT_MISSING, html)
                    .forEach(this::sendEmail);
        }
        return emailsToSend;
    }

    /**
     * The actual scheduled process. Will fetch and send out emails.
     */
    public void runPECNotificationProcess() {
        logger.info("Starting PEC Notification Process");
        emailCount = 0;
        getScheduledEmails(true).forEach(this::sendEmail);
        logger.info("Completed PEC Notification Process");
    }

    public Optional<EmployeeEmail> getInviteEmail(int empId, PersonnelTask task, LocalDateTime dueDate) {
        boolean wasNotifSent = pecNotificationDao.wasNotificationSent(empId, task.getTaskId());
        if (!task.isActive() || wasNotifSent || !task.isNotifiable()) {
            return Optional.empty();
        }
        return Optional.ofNullable(pecEmailUtils.getEmail(empId, INVITE, task, dueDate));
    }

    public void sendCompletionEmail(int empId, PersonnelTask task) {
        if (!task.isNotifiable()) {
            return;
        }
        sendEmail(pecEmailUtils.getEmail(empId, COMPLETION, task, null));
    }

    /**
     * Sends emails. Limited by test mode and PEC notifs enabled.
     */
    public void sendEmail(EmployeeEmail emailInfo) {
        if (emailInfo.getEmployee().isSenator() ||
                (!allPecNotifsEnabled && emailInfo.type() != ADMIN_CODES)) {
            return;
        }
        if (emailInfo.type() == INVITE || emailInfo.type() == REMINDER) {
            if (emailCount >= emailLimit) {
                return;
            }
            emailCount++;
        }
        pecEmailUtils.addStandardHtml(emailInfo);
        String address = emailInfo.getEmployee().getEmail();
        // Keeps spacing consistent and positive.
        String spaces = " ".repeat(Math.max(28 - address.length(), 1));
        String logMessage = "Recipient: %sSubject: %s\n".formatted(address + spaces, emailInfo.subject());
        if (pecTestMode) {
            address = reportEmails.get(0);
        }
        MimeMessage message = sendMailService.newHtmlMessage(address, emailInfo.subject(), emailInfo.html());
        try {
            sendMailService.send(message);
            Files.writeString(emailLogPath, logMessage, CREATE, WRITE, APPEND);
            if (emailInfo.type() == INVITE && !pecTestMode) {
                pecNotificationDao.markNotificationSent(
                        emailInfo.getEmployee().getEmployeeId(),
                        emailInfo.first().getTaskId());
            }
        } catch (Exception e) {
            logger.error("There was an error trying to send the PEC notification email ", e);
        }
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
