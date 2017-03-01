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
    private static final String errorTemplateName = "error";

    @RequestMapping(value = "/404", method = RequestMethod.GET)
    public String pageNotFound(ModelMap modelMap) {
        modelMap.addAttribute("title", "Page Not Found")
                .addAttribute("message", "Sorry, the page you requested could not be found.");
        return errorPage(modelMap);
    }

    @RequestMapping(value = "/restricted")
    public String accessRestrictedPage(ModelMap modelMap) {
        modelMap.addAttribute("title", "Access Restricted")
                .addAttribute("message", "ESS is currently only available to a subset of Senate employees for testing.");
        return warningPage(modelMap);
    }

    @RequestMapping(value = "/authz", method = RequestMethod.GET)
    public String authzError(ModelMap modelMap) {
        modelMap.addAttribute("title", "Authorization Error")
                .addAttribute("message",
                        "An error occurred during ESS user authorization at " + LocalDateTime.now() + "<br>" +
                         "Please report this to the STS Help Line at Senate x2011");
        return errorPage(modelMap);
    }

    /* --- Internal Methods ---*/

    private String warningPage(ModelMap modelMap) {
        modelMap.addAttribute("level", "warn");
        return errorTemplateName;
    }

    private String errorPage(ModelMap modelMap) {
        modelMap.addAttribute("level", "error");
        return errorTemplateName;
    }
}