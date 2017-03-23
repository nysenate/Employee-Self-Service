package gov.nysenate.ess.web.controller.page;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

/**
 * Handles requests to the Help page.
 */
@Controller
@RequestMapping("/help/**")
public class HelpPageCtrl extends BaseEssPageCtrl
{
    @RequestMapping(method = {RequestMethod.GET, RequestMethod.HEAD})
    @Override
    String mainPage(ModelMap modelMap, HttpServletRequest request) {
        addCommonModelMapData(modelMap);
        return "help";
    }
}
