package gov.nysenate.ess.web.controller.page;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jakarta.servlet.http.HttpServletRequest;

import static gov.nysenate.ess.web.controller.page.FrontendFramework.REACT;

@Controller
@RequestMapping("/supply/**")
public class SupplyPageCtrl {
    private final PageCtrlUtils pageCtrlUtils;
    private final boolean serveReact;

    @Autowired
    public SupplyPageCtrl(PageCtrlUtils pageCtrlUtils,
                          @Value("${frontend.supply.framework:}") String frontendFramework) {
        this.pageCtrlUtils = pageCtrlUtils;
        this.serveReact = FrontendFramework.fromProperty(
                "frontend.supply.framework", frontendFramework) == REACT;
    }

    @RequestMapping(method = {RequestMethod.GET, RequestMethod.HEAD})
    public Object supplyPage(ModelMap modelMap, HttpServletRequest request) {
        if (serveReact) {
            return "forward:/assets/dist/index.html";
        } else {
            modelMap.addAllAttributes(pageCtrlUtils.commonPageData());
            return "supply";
        }
    }
}
