package gov.nysenate.ess.web.controller.page;

import org.springframework.beans.factory.annotation.Autowired;
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
public class HelpPageCtrl {

    private final PageCtrlUtils pageCtrlUtils;

    @Autowired
    public HelpPageCtrl(PageCtrlUtils pageCtrlUtils) {
        this.pageCtrlUtils = pageCtrlUtils;
    }

    @RequestMapping(method = {RequestMethod.GET, RequestMethod.HEAD})
    public Object helpPage(ModelMap modelMap, HttpServletRequest request) {
        modelMap.addAllAttributes(pageCtrlUtils.commonPageData());
        return "help";
    }
}
