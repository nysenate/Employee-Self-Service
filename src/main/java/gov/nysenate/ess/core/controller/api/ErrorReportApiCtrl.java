package gov.nysenate.ess.core.controller.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import gov.nysenate.ess.core.client.parameter.ErrorReport;
import gov.nysenate.ess.core.client.response.base.SimpleResponse;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.service.mail.SendMailService;
import gov.nysenate.ess.core.service.personnel.EssCachedEmployeeInfoService;
import gov.nysenate.ess.core.service.template.EssTemplateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * Created by senateuser on 2016/11/4.
 */
@RestController
@RequestMapping(BaseRestApiCtrl.REST_PATH + "/report")
public class ErrorReportApiCtrl extends BaseRestApiCtrl {

    private static final Logger logger = LoggerFactory.getLogger(ErrorReportApiCtrl.class);

    private static final String subjectPrefix = "ESS Error Report from ";

    @Value("${report.email}") private String reportEmail;
    @Value("${freemarker.core.templates.error_report:error_report.ftlh}")
    private String emailTemplateName;

    @Autowired private Configuration freemarkerCfg;
    @Autowired private SendMailService sendMailService;
    @Autowired private EssCachedEmployeeInfoService essCachedEmployeeInfoService;

    @Resource(name = "jsonObjectMapper") ObjectMapper objectMapper;

    /**
     * Receives an error report and sends an email notification
     *
     * @param errorReport {@link ErrorReport}
     * @return {@link SimpleResponse}
     * @throws JsonProcessingException
     */
    @RequestMapping(value = "/error", method = RequestMethod.POST, consumes = "application/json")
    public SimpleResponse report(@RequestBody ErrorReport errorReport) throws JsonProcessingException {
        try {
            MimeMessage message = getErrorMessage(errorReport);
            sendMailService.send(message);
        } catch (Exception e) {
            logger.error("Exception occurred during processing of error report submission!", e);
            return new SimpleResponse(false, e.getMessage(), e.getClass().getSimpleName());
        }

        return new SimpleResponse(true, "successful", "successful");
    }

    /* --- Internal Methods --- */

    private MimeMessage getErrorMessage(ErrorReport errorReport) throws JsonProcessingException {
        Employee employee = essCachedEmployeeInfoService.getEmployee(errorReport.getUser());

        String subject = subjectPrefix + employee.getFullName();
        String message = getMessageBody(errorReport, employee);

        return sendMailService.newHtmlMessage(reportEmail, subject, message);
    }

    private String getMessageBody(ErrorReport errorReport, Employee employee) throws JsonProcessingException {
        LocalDateTime now = LocalDateTime.now();

        String details = objectMapper.writerWithDefaultPrettyPrinter()
                .writeValueAsString(errorReport.getDetails());

        Map dataModel = ImmutableMap.of(
                "employee", employee,
                "timestamp", now,
                "errorReport", errorReport,
                "details", details
        );

        StringWriter out = new StringWriter();
        try {
            Template emailTemplate = freemarkerCfg.getTemplate(emailTemplateName);
            emailTemplate.process(dataModel, out);
        } catch (IOException | TemplateException ex) {
            throw new EssTemplateException(emailTemplateName, ex);
        }

        return out.toString();
    }

}
