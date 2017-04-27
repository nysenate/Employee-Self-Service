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
 * Creates a MimeMessage used to send an email notifying a user their requisition has been rejected.
 */
@Service
public class RejectRequisitionEmail {

    private SendMailService sendMailService;
    private Configuration freemarkerCfg;
    private static final String template = "requisition_rejection.ftlh";
    private static final String subject = "Your supply requisition request has been rejected.";
    private static String domainUrl;

    @Autowired
    public RejectRequisitionEmail(SendMailService sendMailService, Configuration freemarkerCfg,
                                  @Value("${domain.url}") final String domainUrl) {
        this.sendMailService = sendMailService;
        this.freemarkerCfg = freemarkerCfg;
        RejectRequisitionEmail.domainUrl = domainUrl;
    }

    public MimeMessage generateRejectionEmail(Requisition requisition, String toEmail) {
        String body = generateBody(requisition);
        return sendMailService.newHtmlMessage(toEmail, subject, body);
    }

    private String generateBody(Requisition requisition) {
        StringWriter out = new StringWriter();
        Map dataModel = ImmutableMap.of("requisition", requisition, "domainUrl", domainUrl);
        try {
            Template emailTemplate = freemarkerCfg.getTemplate(template);
            emailTemplate.process(dataModel, out);
        } catch (IOException | TemplateException e) {
            throw new EssTemplateException(template, e);
        }
        return out.toString();
    }
}
