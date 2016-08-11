package gov.nysenate.ess.seta.service.notification;

import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimaps;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.service.mail.SendMailService;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import gov.nysenate.ess.core.service.template.EssTemplateException;
import gov.nysenate.ess.seta.model.attendance.TimeRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * {@inheritDoc}
 * Uses Freemarker {@link Template templates} and {@link SendMailService}
 * to implement functionality of {@link TimeRecordEmailService}
 */
@Service
public class EssTimeRecordEmailService implements TimeRecordEmailService {

    @Autowired private SendMailService sendMailService;
    @Autowired private Configuration freemarkerCfg;
    @Autowired private EmployeeInfoService empInfoService;

    @Value("${freemarker.time.templates.time_record_reminder:time_record_reminder.ftlh}")
    private String emailTemplateName;

    private static final String reminderEmailSubject = "Time and Attendance records need to be submitted.";

    private Template emailTemplate;


    /**
     * Initializes the time record email template
     * @throws IOException
     */
    @PostConstruct
    public void init() throws IOException {
        emailTemplate = freemarkerCfg.getTemplate(emailTemplateName);
    }

    /** {@inheritDoc} */
    @Override
    public void sendEmailReminders(Integer supId, Collection<TimeRecord> timeRecords) {
        // Group records by employee
        ImmutableListMultimap<Integer, TimeRecord> timeRecordMultimap =
                Multimaps.index(timeRecords, TimeRecord::getEmployeeId);
        // Generate messages for each employee
        ArrayList<MimeMessage> messages =
                timeRecordMultimap.asMap().entrySet().stream()
                        .map(entry -> generateReminderEmail(supId, entry.getKey(), entry.getValue()))
                        .collect(Collectors.toCollection(ArrayList::new));
        // Send messages
        sendMailService.sendMessages(messages);
    }

    /** --- Internal Methods --- */

    /**
     * Generate a {@link MimeMessage} to remind an employee to submit a time record.
     * Uses supervisor and employee data, the employee's time records,
     * and the email reminder template
     *
     * @param supId Integer - supervisor id
     * @param empId Integer - employee id
     * @param timeRecords {@link Collection<TimeRecord>} - employee's time records
     * @return {@link MimeMessage} - Time record submission reminder email
     */
    private MimeMessage generateReminderEmail(Integer supId, Integer empId, Collection<TimeRecord> timeRecords) {
        Employee employee = empInfoService.getEmployee(empId);
        Employee supervisor = empInfoService.getEmployee(supId);
        String to = employee.getEmail();
        String from = supervisor.getEmail();
        String subject = reminderEmailSubject;
        String body = getEmailBody(employee, timeRecords);

        return sendMailService.newHtmlMessage(to, from, subject, body);
    }

    /**
     * Generate a templated HTML email body.  Use the employee and time records as data
     * @param employee {@link Employee} - target employee
     * @param timeRecords {@link Collection<TimeRecord>} - employee's time records
     * @return String - html email reminder message
     */
    private String getEmailBody(Employee employee, Collection<TimeRecord> timeRecords) {
        StringWriter out = new StringWriter();
        Map dataModel = ImmutableMap.of("employee", employee, "timeRecords", timeRecords);
        try {
            emailTemplate.process(dataModel, out);
        } catch (IOException | TemplateException ex) {
            throw new EssTemplateException(emailTemplate, ex);
        }
        return out.toString();
    }

}
