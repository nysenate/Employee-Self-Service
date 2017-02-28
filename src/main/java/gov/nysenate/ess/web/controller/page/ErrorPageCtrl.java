package gov.nysenate.ess.web.controller.page;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/error")
public class ErrorPageCtrl
{
    @RequestMapping(value = "/404", method = RequestMethod.GET)
    public String pageNotFound(ModelMap modelMap) {
        modelMap.put("message", "Sorry, the page you requested could not be found.");
        return "error";
    }

    @RequestMapping(value = "/restricted")
    public String accessRestrictedPage() {
        return "restricted";
    }

    @RequestMapping(value = "/authz-error", method = RequestMethod.GET)
    public String authzError(ModelMap modelMap) {
        modelMap.put("message",
                "An error occurred during ESS user authorization at " + LocalDateTime.now() + "\n" +
                "Please report this to the STS Help Line at Senate x2011");
        return "error";
    }
}