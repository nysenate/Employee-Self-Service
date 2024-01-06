package gov.nysenate.ess.supply.notification;

import com.google.common.collect.ImmutableMap;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import gov.nysenate.ess.core.service.mail.SendMailService;
import gov.nysenate.ess.core.service.template.EssTemplateException;
import gov.nysenate.ess.supply.requisition.model.Requisition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

/**
 * Generates a MimeMessage used to confirm a requisition order.
 */
@Service
public class CustomerConfirmationEmail {

    private final SendMailService sendMailService;
    private final Configuration freemarkerCfg;
    @Value("${domain.url}")
    private String domainUrl;
    private static final String template = "requisition_confirmation.ftlh";
    private static final String subject = "Your supply requisition request has been submitted.";

    @Autowired
    public CustomerConfirmationEmail(SendMailService sendMailService, Configuration freemarkerCfg) {
        this.sendMailService = sendMailService;
        this.freemarkerCfg = freemarkerCfg;
    }

    public MimeMessage generateConfirmationEmail(Requisition requisition, String toEmail) {
        String body = generateBody(requisition);
        return sendMailService.newHtmlMessage(toEmail, subject, body);
    }

    private String generateBody(Requisition requisition) {
        StringWriter out = new StringWriter();
        Map<String, Object> dataModel = ImmutableMap.of("requisition", requisition, "domainUrl", domainUrl);
        try {
            Template emailTemplate = freemarkerCfg.getTemplate(template);
            emailTemplate.process(dataModel, out);
        } catch (IOException | TemplateException e) {
            throw new EssTemplateException(template, e);
        }
        return out.toString();
    }
}
