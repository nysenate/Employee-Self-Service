package gov.nysenate.ess.web.controller.page;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

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
}