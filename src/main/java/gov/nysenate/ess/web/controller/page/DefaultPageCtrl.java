package gov.nysenate.ess.web.controller.page;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/")
public class DefaultPageCtrl
{
    private static final Logger logger = LoggerFactory.getLogger(DefaultPageCtrl.class);

    @RequestMapping(method = {RequestMethod.GET, RequestMethod.HEAD})
    public String defaultRedirect() {
        return "redirect:/time";
    }

}
