package gov.nysenate.ess.time.service.notification;

import com.google.common.collect.ImmutableMap;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.service.mail.SendMailService;
import gov.nysenate.ess.core.service.template.EssTemplateException;
import gov.nysenate.ess.time.model.attendance.TimeRecord;
import gov.nysenate.ess.time.model.notification.EssTimeRecordEmailReminder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.StringWriter;
import java.util.*;

/**
 * {@inheritDoc}
 * Uses Freemarker {@link Template templates} and {@link SendMailService}
 * to implement functionality of {@link RecordReminderEmailService}
 */
@Service
public class EssRecordReminderEmailService implements RecordReminderEmailService {

    private static final Logger logger = LoggerFactory.getLogger(EssRecordReminderEmailService.class);

    @Autowired private SendMailService sendMailService;
    @Autowired private Configuration freemarkerCfg;

    @Value("${freemarker.time.templates.time_record_reminder:time_record_reminder.ftlh}")
    private String emailTemplateName;

    private static final String reminderEmailSubject = "Time and Attendance records need to be submitted";

    /**
     * {@inheritDoc}
     */
    @Override
    public List<EssTimeRecordEmailReminder> sendEmailReminders(List<EssTimeRecordEmailReminder> reminders) {
        for (EssTimeRecordEmailReminder reminder : reminders) {
            try {
                // Generate and send each email individually so we know who received or didn't receive an email in the case of errors.
                if (reminder.getEmployee().isActive()) {
                    // Only send if employee is active.
                    MimeMessage message = generateReminderEmail(reminder.getEmployee(), reminder.getTimeRecords());
                    sendMailService.sendMessages(Collections.singletonList(message));
                    reminder.setWasReminderSent(true);
                }
            } catch (Exception ex) {
                logger.info("Unable to send time record email reminder to empId: '"
                        + reminder.getEmployee().getEmployeeId() + "'.", ex);
            }
        }

        return reminders;
    }

    /** --- Internal Methods --- */

    /**
     * Generate a {@link MimeMessage} to remind an employee to submit a time record.
     * Uses employee data and the employee's time records,
     * and the email reminder template
     *
     * @param timeRecords {@link Collection<TimeRecord>} - employee's time records
     * @return {@link MimeMessage} - Time record submission reminder email
     */
    private MimeMessage generateReminderEmail(Employee employee, Collection<TimeRecord> timeRecords) {
        String to = employee.getEmail();
        String body = getEmailBody(employee, timeRecords);

        return sendMailService.newHtmlMessage(to, reminderEmailSubject, body);
    }

    /**
     * Generate a templated HTML email body.  Use the employee and time records as data
     *
     * @param employee    {@link Employee} - target employee
     * @param timeRecords {@link Collection<TimeRecord>} - employee's time records
     * @return String - html email reminder message
     */
    private String getEmailBody(Employee employee, Collection<TimeRecord> timeRecords) {
        StringWriter out = new StringWriter();
        // Ensure the records are ordered by date
        List<TimeRecord> sortedTimeRecords = new ArrayList<>(timeRecords);
        sortedTimeRecords.sort(Comparator.comparing(TimeRecord::getBeginDate));
        Map dataModel = ImmutableMap.of("employee", employee, "timeRecords", sortedTimeRecords);
        try {
            Template emailTemplate = freemarkerCfg.getTemplate(emailTemplateName);
            emailTemplate.process(dataModel, out);
        } catch (IOException | TemplateException ex) {
            throw new EssTemplateException(emailTemplateName, ex);
        }
        return out.toString();
    }
}
