package gov.nysenate.ess.travel.notifications.email;

import com.google.common.collect.ImmutableMap;
import freemarker.template.*;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.service.mail.SendMailService;
import gov.nysenate.ess.travel.application.TravelApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

@Service
public class TravelAppDisapprovalEmail {

    private SendMailService sendMailService;
    private Configuration freemarkerCfg;
    private static final String template = "travel_app_disapproval_notice.ftlh";
    private String domainUrl;

    @Autowired
    public TravelAppDisapprovalEmail(SendMailService sendMailService, Configuration freemarkerCfg,
                                     @Value("${domain.url}") String domainUrl) {
        this.sendMailService = sendMailService;
        this.freemarkerCfg = freemarkerCfg;
        this.domainUrl = domainUrl;
    }

    public MimeMessage createEmail(TravelApplication app, Employee toEmployee,
                                   Employee disapprover, String reason) {
        String subject = generateSubject(app);
        String body = generateBody(app, toEmployee, disapprover, reason);
        return sendMailService.newHtmlMessage(toEmployee.getEmail(), subject, body);
    }

    private String generateSubject(TravelApplication app) {
        StringBuilder subject = new StringBuilder("Disapproved Travel Application for ");
        subject.append(app.getTraveler().getFullName());
        subject.append(String.format(" on %s ", app.activeAmendment().route().startDate()));
        if (!app.activeAmendment().route().startDate().equals(app.activeAmendment().route().endDate())) {
            subject.append(String.format("- %s ", app.activeAmendment().route().endDate()));
        }
        return subject.toString();
    }

    private String generateBody(TravelApplication app, Employee toEmployee, Employee disapprover, String reason) {
        StringWriter out = new StringWriter();
        Map dataModel = ImmutableMap.builder()
                .put("app", app)
                .put("toEmployee", toEmployee)
                .put("reason", reason)
                .put("disapprover", disapprover)
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
