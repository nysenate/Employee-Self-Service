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

/**
 * Handles requests to the Travel app.
 */
@Controller
@RequestMapping("/travel/**")
public class TravelPageCtrl {
    private static final Logger logger = LoggerFactory.getLogger(TravelPageCtrl.class);
    private final PageCtrlUtils pageCtrlUtils;
    private final boolean serveReact;

    @Autowired
    public TravelPageCtrl(PageCtrlUtils pageCtrlUtils, @Value("${serve.react.travel:false}") boolean serveReact) {
        this.pageCtrlUtils = pageCtrlUtils;
        this.serveReact = serveReact;
    }

    @RequestMapping(method = {RequestMethod.GET, RequestMethod.HEAD})
    public Object travelPage(ModelMap modelMap, HttpServletRequest request) {
        if (serveReact) {
            return new InternalResourceView("assets/dist/index.html");
        } else {
            modelMap.addAllAttributes(pageCtrlUtils.commonPageData());
            return "travel";
        }
	}
}
