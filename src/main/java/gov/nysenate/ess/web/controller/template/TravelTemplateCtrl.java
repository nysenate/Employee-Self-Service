package gov.nysenate.ess.web.controller.template;

import gov.nysenate.ess.travel.authorization.permission.TravelPermission;
import org.apache.shiro.SecurityUtils;
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
    static final String NOT_AUTHORIZED_PAGE = TRAVEL_TMPL_BASE_URL + "/common/error/unauthorized";

    /**
     * Generic mapping to handle all requests that don't require permission.
     * Assumes the request URI equals the location in the WEB_INF/view directory.
     */
    @RequestMapping(value="/**")
    public String travelTemplate(HttpServletRequest request) {
        return request.getRequestURI();
    }

    @RequestMapping(value = "/component/review/app-review")
    public String applicationReview() {
        if (SecurityUtils.getSubject().isPermitted(TravelPermission.TRAVEL_UI_REVIEW.getPermission())) {
            return TRAVEL_TMPL_BASE_URL + "/component/review/app-review";
        }
        return NOT_AUTHORIZED_PAGE;
    }
}
