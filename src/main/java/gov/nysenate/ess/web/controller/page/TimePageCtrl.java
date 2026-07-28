package gov.nysenate.ess.web.controller.page;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import static gov.nysenate.ess.web.controller.page.FrontendFramework.REACT;

/**
 * Handles requests to the Time and Attendance page.
 */
@Controller
@RequestMapping("/time/**")
public class TimePageCtrl {
    private final PageCtrlUtils pageCtrlUtils;
    private final boolean serveReact;

    @Autowired
    public TimePageCtrl(PageCtrlUtils pageCtrlUtils,
                        @Value("${frontend.time.framework:}") String frontendFramework) {
        this.pageCtrlUtils = pageCtrlUtils;
        this.serveReact = FrontendFramework.fromProperty(
                "frontend.time.framework", frontendFramework) == REACT;
    }

    @RequestMapping(method = {RequestMethod.GET, RequestMethod.HEAD})
    public Object timePage(ModelMap modelMap, HttpServletRequest request) {
        if (serveReact) {
            return "forward:/assets/dist/index.html";
        } else {
            modelMap.addAllAttributes(pageCtrlUtils.commonPageData());
            return "time";
        }
    }
}
