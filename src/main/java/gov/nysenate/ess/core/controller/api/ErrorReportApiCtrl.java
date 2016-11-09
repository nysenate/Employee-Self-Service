package gov.nysenate.ess.core.controller.api;

import com.google.common.eventbus.EventBus;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import gov.nysenate.ess.core.client.response.base.SimpleResponse;
import gov.nysenate.ess.core.dao.unit.SessionDao;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.service.mail.SendMailService;
import gov.nysenate.ess.core.service.notification.email.simple.message.SimpleEmailHandler;
import gov.nysenate.ess.core.service.personnel.EssCachedEmployeeInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by senateuser on 2016/11/4.
 */
@RestController
@RequestMapping(BaseRestApiCtrl.REST_PATH + "/report")
public class ErrorReportApiCtrl extends BaseRestApiCtrl {
    @Value("${report.email}")
    private String reportEmail;
    @Autowired
    EventBus eventBus;
    @Autowired
    SendMailService sendMailService;
    @Autowired
    private EssCachedEmployeeInfoService essCachedEmployeeInfoService;


    @RequestMapping(value = "/error", method = RequestMethod.GET)
    public SimpleResponse report(@RequestParam Integer user, @RequestParam String url,@RequestParam String details){
        Employee employee = essCachedEmployeeInfoService.getEmployee(user);
        DateFormat df = new SimpleDateFormat("MM/dd/yy HH:mm:ss");
        Date now = new Date();
        JsonParser jsonParser = new JsonParser();
        JsonObject jo = (JsonObject)jsonParser.parse(details);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try {
            sendMailService.sendMessage(reportEmail, employee.getEmail(),employee.getFullName() +" reports an issues",
                    "Report Time: " + df.format(now)+"\n"+"\n"+
                            "URL: "+ url+"\n"+"\n"+
                            "Error: "+"\n"+
                    gson.toJson(jo)
            );
        }
        catch (Exception e){
            return new SimpleResponse(false,e.getMessage(),e.getClass().getSimpleName());
        }
        return new SimpleResponse(true,"successful","successful");
    }
}
