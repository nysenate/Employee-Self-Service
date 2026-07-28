package gov.nysenate.ess.web.controller.page;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jakarta.servlet.http.HttpServletRequest;

import static gov.nysenate.ess.web.controller.page.FrontendFramework.REACT;

/**
 * Handles requests to the My Info page.
 */
@Controller
@RequestMapping("/myinfo/**")
public class MyInfoPageCtrl {
    private final PageCtrlUtils pageCtrlUtils;
    private final boolean serveReact;

    @Autowired
    public MyInfoPageCtrl(PageCtrlUtils pageCtrlUtils,
                          @Value("${frontend.myinfo.framework:}") String frontendFramework) {
        this.pageCtrlUtils = pageCtrlUtils;
        this.serveReact = FrontendFramework.fromProperty(
                "frontend.myinfo.framework", frontendFramework) == REACT;
    }

    @RequestMapping(method = {RequestMethod.GET, RequestMethod.HEAD})
    public Object myInfoPage(ModelMap modelMap, HttpServletRequest request) {
        if (serveReact) {
            return "forward:/assets/dist/index.html";
        } else {
            modelMap.addAllAttributes(pageCtrlUtils.commonPageData());
            return "myinfo";
        }
    }
}
