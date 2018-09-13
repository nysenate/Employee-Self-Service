package gov.nysenate.ess.time.service.notification;

import com.google.common.collect.ImmutableMap;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.service.mail.SendMailService;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import gov.nysenate.ess.core.service.template.EssTemplateException;
import gov.nysenate.ess.core.util.ShiroUtils;
import gov.nysenate.ess.time.model.attendance.TimeRecord;
import gov.nysenate.ess.time.service.attendance.TimeRecordActionEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.StringWriter;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import static gov.nysenate.ess.time.model.attendance.TimeRecordAction.REJECT;
import static gov.nysenate.ess.time.model.attendance.TimeRecordStatus.DISAPPROVED;

@Service
public class EssDisapprovalEmailService implements DisapprovalEmailService {

    private static final String subjectTemplate = "DISAPPROVED TIMESHEET %s - %s";
    private static final DateTimeFormatter subjectDateFormat = DateTimeFormatter.ofPattern("MM/dd/uu");

    private final SendMailService sendMailService;
    private final Configuration freemarkerCfg;
    private final EmployeeInfoService empInfoService;

    @Value("${freemarker.time.templates.time_record_disapproval_notice:time_record_disapproval_notice.ftlh}")
    private String emailTemplateName;

    @Autowired
    public EssDisapprovalEmailService(SendMailService sendMailService,
                                      Configuration freemarkerCfg,
                                      EmployeeInfoService empInfoService,
                                      EventBus eventBus) {
        this.sendMailService = sendMailService;
        this.freemarkerCfg = freemarkerCfg;
        this.empInfoService = empInfoService;
        eventBus.register(this);
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
     * Detect and notify disapprovals through time record action events.
     * @param event {@link TimeRecordActionEvent}
     */
    @Subscribe
    public void handleTimeRecordActionEvent(TimeRecordActionEvent event) {
        TimeRecord record = event.getTimeRecord();
        if (event.getTimeRecordAction() == REJECT && record.getRecordStatus() == DISAPPROVED) {
            sendRejectionMessage(record, ShiroUtils.getAuthenticatedEmpId());
        }
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
            Template emailTemplate = freemarkerCfg.getTemplate(emailTemplateName);
            emailTemplate.process(dataModel, out);
        } catch (IOException | TemplateException ex) {
            throw new EssTemplateException(emailTemplateName, ex);
        }
        return out.toString();
    }
}
