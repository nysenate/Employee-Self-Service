package gov.nysenate.ess.time.service.notification;

import com.google.common.collect.ImmutableMap;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.service.mail.SendMailService;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import gov.nysenate.ess.core.service.template.EssTemplateException;
import gov.nysenate.ess.time.model.attendance.TimeRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.StringWriter;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Map;

import static gov.nysenate.ess.time.model.attendance.TimeRecordStatus.DISAPPROVED;

@Service
public class EssDisapprovalEmailService implements DisapprovalEmailService {

    private static final String subjectTemplate = "DISAPPROVED TIMESHEET %s - %s";
    private static final DateTimeFormatter subjectDateFormat = DateTimeFormatter.ofPattern("MM/dd/uu");

    @Autowired private SendMailService sendMailService;
    @Autowired private Configuration freemarkerCfg;

    @Autowired private EmployeeInfoService empInfoService;

    private Template emailTemplate;

    @Value("${freemarker.time.templates.time_record_disapproval_notice:time_record_disapproval_notice.ftlh}")
    private String emailTemplateName;

    /**
     * Initializes the time record email template
     */
    @PostConstruct
    public void init() throws IOException {
        emailTemplate = freemarkerCfg.getTemplate(emailTemplateName);
    }

    @Override
    public void sendRejectionMessage(TimeRecord rejectedRecord, int rejectorId) {
        if (DISAPPROVED != rejectedRecord.getRecordStatus()) {
            throw new IllegalStateException("Attempt to send disapproval message for non-disapproved timesheet");
        }
        MimeMessage rejectionEmail = generateRejectionEmail(rejectorId, rejectedRecord);
        sendMailService.send(rejectionEmail);
    }

    /**
     * Generate a {@link MimeMessage} notifying an employee of their time record's disapproval
     * Uses supervisor and employee data, the employee's time records,
     * and the email reminder template
     *
     * @param rejectorId Integer - rejector id
     * @param timeRecord {@link TimeRecord} - time records
     * @return {@link MimeMessage} - Time record disapproval email
     */
    private MimeMessage generateRejectionEmail(Integer rejectorId, TimeRecord timeRecord) {
        Employee employee = empInfoService.getEmployee(timeRecord.getEmployeeId());
        Employee rejector = empInfoService.getEmployee(rejectorId);
        String to = employee.getEmail();
        String subject = String.format(subjectTemplate,
                subjectDateFormat.format(timeRecord.getBeginDate()),
                subjectDateFormat.format(timeRecord.getEndDate()));
        String body = getEmailBody(employee, rejector, timeRecord);

        return sendMailService.newHtmlMessage(to, subject, body);
    }

    private String getEmailBody(Employee employee, Employee rejector, TimeRecord timeRecord) {
        StringWriter out = new StringWriter();

        Map dataModel = ImmutableMap.of("employee", employee, "rejector", rejector, "timeRecord", timeRecord);
        try {
            emailTemplate.process(dataModel, out);
        } catch (IOException | TemplateException ex) {
            throw new EssTemplateException(emailTemplate, ex);
        }
        return out.toString();
    }
}
