package gov.nysenate.ess.supply.notification;

import com.google.common.collect.ImmutableMap;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import gov.nysenate.ess.core.service.mail.SendMailService;
import gov.nysenate.ess.core.service.template.EssTemplateException;
import gov.nysenate.ess.supply.requisition.model.Requisition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.StringWriter;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * Generates a MimeMessage used to inform supply employees that a new requisition has been placed.
 */
@Service
public class NewRequisitionEmail {

    private SendMailService sendMailService;
    private Configuration freemarkerCfg;
    private static final String template = "new_requisition_notification.ftlh";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yy h:mm a");

    @Autowired
    public NewRequisitionEmail(SendMailService sendMailService, Configuration freemarkerCfg) {
        this.sendMailService = sendMailService;
        this.freemarkerCfg = freemarkerCfg;
    }

    public MimeMessage generateNewRequisitionEmail(Requisition requisition, String toEmail) {
        String subject = generateSubject(requisition);
        String body = generateBody(requisition);
        return sendMailService.newHtmlMessage(toEmail, subject, body);
    }

    private String generateSubject(Requisition requisition) {
        return "A new requisition was submitted at "
                + requisition.getOrderedDateTime().format(formatter);
    }

    private String generateBody(Requisition requisition) {
        StringWriter out = new StringWriter();
        Map dataModel = ImmutableMap.of("requisition", requisition,
                "orderedDateTime", requisition.getOrderedDateTime().format(formatter));
        try {
            Template emailTemplate = freemarkerCfg.getTemplate(template);
            emailTemplate.process(dataModel, out);
        } catch (IOException | TemplateException e) {
            throw new EssTemplateException(template, e);
        }
        return out.toString();
    }
}
