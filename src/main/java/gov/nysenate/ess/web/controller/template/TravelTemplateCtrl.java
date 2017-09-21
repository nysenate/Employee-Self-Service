package gov.nysenate.ess.web.controller.template;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping(TravelTemplateCtrl.TRAVEL_TMPL_BASE_URL)
public class TravelTemplateCtrl extends BaseTemplateCtrl {

    private static final Logger logger = LoggerFactory.getLogger(TimeTemplateCtrl.class);

    static final String TRAVEL_TMPL_BASE_URL = TMPL_BASE_URL + "/travel";

    /**
     * Generic mapping to handle all requests that don't require permission.
     * Assumes the request URI equals the location in the WEB_INF/view directory.
     */
    @RequestMapping(value="/**")
    public String travelTemplate(HttpServletRequest request) {
        return request.getRequestURI();
    }
}
