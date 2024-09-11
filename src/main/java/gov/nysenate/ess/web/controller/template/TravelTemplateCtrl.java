package gov.nysenate.ess.web.controller.template;

import gov.nysenate.ess.travel.authorization.permission.SimpleTravelPermission;
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

    @RequestMapping(value = "/component/review-travel-app/review-travel-app")
    public String applicationReview(HttpServletRequest request) {
        if (SecurityUtils.getSubject().isPermitted(SimpleTravelPermission.TRAVEL_UI_REVIEW.getPermission())) {
            return request.getRequestURI();
        }
        return NOT_AUTHORIZED_PAGE;
    }

    @RequestMapping(value = "/component/review-history/review-history")
    public String reviewHistory(HttpServletRequest request) {
        if (SecurityUtils.getSubject().isPermitted(SimpleTravelPermission.TRAVEL_UI_REVIEW_HISTORY.getPermission())) {
            return request.getRequestURI();
        }
        return NOT_AUTHORIZED_PAGE;
    }

    @RequestMapping(value = "/component/delegation/delegation")
    public String assignDelegates(HttpServletRequest request) {
         if (SecurityUtils.getSubject().isPermitted(SimpleTravelPermission.TRAVEL_ASSIGN_DELEGATES.getPermission())) {
            return request.getRequestURI();
        }
        return NOT_AUTHORIZED_PAGE;
    }

    @RequestMapping(value = "/component/reconcile-travel/reconcile-travel")
    public String reconcileTravel(HttpServletRequest request) {
        if (SecurityUtils.getSubject().isPermitted(SimpleTravelPermission.TRAVEL_UI_RECONCILE_TRAVEL.getPermission())) {
            return request.getRequestURI();
        }
        return NOT_AUTHORIZED_PAGE;
    }
}
