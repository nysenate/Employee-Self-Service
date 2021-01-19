package gov.nysenate.ess.travel.notifications.email;

import com.google.common.collect.ImmutableMap;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.service.mail.SendMailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

@Service
public class TravelAppEditEmail {

    private SendMailService sendMailService;
    private Configuration freemarkerCfg;
    private static final String template = "travel_app_edited_notice.ftlh";
    private static String domainUrl;

    @Autowired
    public TravelAppEditEmail(SendMailService sendMailService, Configuration freemarkerCfg,
                              @Value("${domain.url}") final String domainUrl) {
        this.sendMailService = sendMailService;
        this.freemarkerCfg = freemarkerCfg;
        this.domainUrl = domainUrl;
    }

    public MimeMessage createEmail(TravelAppEmailView view, Employee recipient) {
        String subject = generateSubject(view);
        String body = generateBody(view, recipient);
        return sendMailService.newHtmlMessage(recipient.getEmail(), subject, body);
    }

    private String generateSubject(TravelAppEmailView view) {
        return "Travel Application for " + view.getTravelerFullName() +
                " on " + view.getDatesOfTravel() + " has been edited";
    }

    private String generateBody(TravelAppEmailView view, Employee recipient) {
        StringWriter out = new StringWriter();
        Map dataModel = ImmutableMap.builder()
                .put("view", view)
                .put("recipient", recipient)
                .put("domainUrl", domainUrl)
                .build();
        try {
            Template emailTemplate = freemarkerCfg.getTemplate(template);
            emailTemplate.process(dataModel, out);
        } catch (IOException | TemplateException e) {
            e.printStackTrace();
        }
        return out.toString();
    }
}
