package gov.nysenate.ess.web.controller.page;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.view.InternalResourceView;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/supply/**")
public class SupplyPageCtrl {
    private static final Logger logger = LoggerFactory.getLogger(SupplyPageCtrl.class);
    private final PageCtrlUtils pageCtrlUtils;
    private final boolean serveReact;

    @Autowired
    public SupplyPageCtrl(PageCtrlUtils pageCtrlUtils, @Value("${serve.react.supply:false}") boolean serveReact) {
        this.pageCtrlUtils = pageCtrlUtils;
        this.serveReact = serveReact;
    }

    @RequestMapping(method = {RequestMethod.GET, RequestMethod.HEAD})
    public Object supplyPage(ModelMap modelMap, HttpServletRequest request) {
        if (serveReact) {
            return new InternalResourceView("assets/dist/index.html");
        } else {
            modelMap.addAllAttributes(pageCtrlUtils.commonPageData());
            return "supply";
        }
    }
}
