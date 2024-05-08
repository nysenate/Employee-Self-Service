package gov.nysenate.ess.core.service.pec.notification;

import gov.nysenate.ess.core.dao.pec.notification.PECNotificationDao;
import gov.nysenate.ess.core.model.pec.PersonnelTask;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.service.mail.SendMailService;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static gov.nysenate.ess.core.service.pec.notification.PecEmailType.*;
import static java.nio.file.StandardOpenOption.*;

@Service
public class PECNotificationService {
    private static final Logger logger = LoggerFactory.getLogger(PECNotificationService.class);

    private final SendMailService sendMailService;
    private final PecEmailUtils pecEmailUtils;
    private final PECNotificationDao pecNotificationDao;

    private final List<String> reportEmails;
    private final boolean pecTestMode;
    private final double emailLimit;
    private final Path emailLogPath;
    private double emailCount = 0;

    @Value("${scheduler.pec.notifs.enabled:false}")
    private boolean pecNotifsEnabled;
    @Value("${all.pec.notifs.enabled:false}")
    private boolean allPecNotifsEnabled;

    public PECNotificationService(SendMailService sendMailService,
                                  PecEmailUtils pecEmailUtils,
                                  PECNotificationDao pecNotificationDao,
                                  @Value("${report.email}") String reportEmailList,
                                  @Value("${pec.test.mode:true}") boolean pecTestMode,
                                  @Value("${data.dir}") String dataDir,
                                  @Value("${data.log_subdir:log}") String logDir) {
        this.sendMailService = sendMailService;
        this.pecEmailUtils = pecEmailUtils;
        this.pecNotificationDao = pecNotificationDao;
        this.reportEmails = List.of(reportEmailList.replaceAll(" ", "").split(","));
        this.pecTestMode = pecTestMode;
        this.emailLimit = pecTestMode ? 15 : Double.POSITIVE_INFINITY;
        new File(dataDir + "/" + logDir).mkdir();
        this.emailLogPath = Path.of(dataDir, logDir, "emailLog.txt");
    }

    @Scheduled(cron = "${scheduler.pec.notifs.cron}")
    public void runUpdateMethods() {
        if (pecNotifsEnabled) {
            sendReminderEmails();
        }
    }

    /**
     * Fetches data on emails that would be sent right now, if the relevant cron ran.
     * Notably does NOT include invite emails.
     * @param sendAdminEmails whether to send emails about employees with missing emails.
     * @return List of email information.
     */
    public List<EmployeeEmail> getReminderEmails(boolean sendAdminEmails, boolean allNotifs) {
        // Document all employees with missing info to report to admins
        List<String> missingEmails = new ArrayList<>();
        List<EmployeeEmail> emailsToSend = new ArrayList<>();
        for (var entry : pecEmailUtils.getNotifiableTaskMap(allNotifs).entrySet()) {
            // Continue processing employee if they have outstanding assignments and a valid email
            Employee employee = entry.getKey();
            if (Strings.isBlank(employee.getEmail())) {
                missingEmails.add("NAME: " + employee.getFullName() +  " EMPID: " + employee.getEmployeeId());
                logger.warn("Employee %s #%d is missing an email! No notification will be sent to them."
                        .formatted(employee.getFullName(), employee.getEmployeeId()));
            } else {
                emailsToSend.add(pecEmailUtils.getEmail(REMINDER, employee, entry.getValue()));
            }
        }
        if (!missingEmails.isEmpty() && sendAdminEmails) {
            pecEmailUtils.getEmails(reportEmails, REPORT_MISSING, Optional.empty(), missingEmails)
                    .forEach(this::sendEmail);
        }
        return emailsToSend;
    }

    /**
     * The actual scheduled process. Will fetch and send out emails.
     */
    public void sendReminderEmails() {
        logger.info("Starting PEC Reminder Process");
        emailCount = 0;
        getReminderEmails(true, false).forEach(this::sendEmail);
        logger.info("Completed PEC Reminder Process");
    }

    public void sendInviteEmails(List<AssignmentWithTask> assigned) {
        getInviteEmails(assigned).forEach(this::sendEmail);
    }

    public List<EmployeeEmail> getInviteEmails(List<AssignmentWithTask> assigned) {
        return assigned.stream().filter(data -> data.task().isNotifiable())
                .map(task -> pecEmailUtils.getEmail(INVITE, Optional.empty(), task))
                .collect(Collectors.toList());
    }

    public void sendCompletionEmail(int empId, PersonnelTask task) {
        if (!task.isNotifiable()) {
            return;
        }
        sendEmail(pecEmailUtils.getEmail(COMPLETION, Optional.empty(), new AssignmentWithTask(empId, task)));
    }

    public void sendCodeEmail(List<String> emails, String code1, String code2, PersonnelTask task, String startDate, String endDate) {
        pecEmailUtils.getEmails(emails, ADMIN_CODES, Optional.of(task), List.of(code1, code2, startDate, endDate))
                .forEach(this::sendEmail);
    }

    /**
     * Sends emails. Limited by test mode and PEC notifs enabled.
     */
    private void sendEmail(EmployeeEmail emailInfo) {
        if (emailInfo.employee().isSenator() ||
                (!allPecNotifsEnabled && emailInfo.type() != ADMIN_CODES)) {
            return;
        }
        if (emailInfo.type() == INVITE || emailInfo.type() == REMINDER) {
            if (emailCount >= emailLimit) {
                return;
            }
            emailCount++;
        }
        String address = emailInfo.employee().getEmail();
        // Keeps spacing consistent and positive.
        String spaces = " ".repeat(Math.max(28 - address.length(), 1));
        String logMessage = "Recipient: %sSubject: %s\n".formatted(address + spaces, emailInfo.subject());
        String html = emailInfo.html();
        if (emailInfo.type() != ADMIN_CODES && pecTestMode) {
            address = reportEmails.get(0);
            html += "<br> Employee ID: #" + emailInfo.employee().getEmployeeId() +
                    "<br> Email: " + emailInfo.employee().getEmail();
        }
        MimeMessage message = sendMailService.newHtmlMessage(address, emailInfo.subject(), html);
        try {
            sendMailService.send(message);
            Files.writeString(emailLogPath, logMessage, CREATE, WRITE, APPEND);
            if (emailInfo.type() == INVITE) {
                pecNotificationDao.markNotificationSent(
                        emailInfo.employee().getEmployeeId(),
                        emailInfo.first().getTaskId());
            }
        } catch (Exception e) {
            logger.error("There was an error trying to send the PEC notification email ", e);
        }
    }
}
