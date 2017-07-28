package gov.nysenate.ess.time.service.notification;

import com.google.common.collect.ImmutableMap;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import gov.nysenate.ess.core.service.mail.SendMailService;
import gov.nysenate.ess.core.service.template.EssTemplateException;
import gov.nysenate.ess.time.service.attendance.TimeRecordManagerError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;

/** {@inheritDoc} */
@Service
public class EssTimeRecordManagerEmailService implements TimeRecordManagerEmailService {

    @Autowired private SendMailService sendMailService;
    @Autowired private Configuration freemarkerCfg;

    @Value("${report.email}") private String reportEmail;

    @Value("${freemarker.time.templates.trm_error:trm_error.ftlh}")
    private String errorEmailTemplateName;

    private static final String reminderEmailSubject = "Time Record Manager Errors: ";

    /** {@inheritDoc}
     * @param exceptions*/
    @Override
    public void sendTrmErrorNotification(Collection<TimeRecordManagerError> exceptions) {

        if (exceptions.isEmpty()) {
            return;
        }

        String to = reportEmail;
        String subject = reminderEmailSubject + exceptions.size();
        String body = getErrorEmailBody(exceptions);

        MimeMessage message = sendMailService.newHtmlMessage(to, subject, body);

        sendMailService.send(message);
    }

    /* --- Internal Methods --- */

    /**
     * Generates an html email body from the given error
     *
     * @param exceptions {@link Collection< TimeRecordManagerError >}
     * @return String
     */
    private String getErrorEmailBody(Collection<TimeRecordManagerError> exceptions) {
        StringWriter out = new StringWriter();
        Map dataModel = ImmutableMap.builder()
                .put("exceptions", exceptions)
                .put("reportTime", LocalDateTime.now())
                .build();
        try {
            Template emailTemplate = freemarkerCfg.getTemplate(errorEmailTemplateName);
            emailTemplate.process(dataModel, out);
        } catch (IOException | TemplateException ex) {
            throw new EssTemplateException(errorEmailTemplateName, ex);
        }
        return out.toString();
    }
}
