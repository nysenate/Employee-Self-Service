package gov.nysenate.ess.core.department;

import com.google.common.collect.ImmutableMap;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import gov.nysenate.ess.core.service.mail.SendMailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;
import java.util.Set;

@Service
public class MissingDeptHdEmail {

    private SendMailService sendMailService;
    private Configuration freemarkerCfg;
    private static final String template = "missing_dept_head_notice.ftlh";

    @Autowired
    public MissingDeptHdEmail(SendMailService sendMailService, Configuration freemarkerCfg) {
        this.sendMailService = sendMailService;
        this.freemarkerCfg = freemarkerCfg;
    }

    public MimeMessage createEmail(Set<DepartmentView> departmentViews, String recipient) {
        return sendMailService.newHtmlMessage(recipient,
                "Warning: Departments in ESS are missing department heads",
                generateBody(departmentViews));
    }

    private String generateBody(Set<DepartmentView> departmentViews) {
        StringWriter out = new StringWriter();
        Map dataModel = ImmutableMap.builder()
                .put("departments", departmentViews)
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
