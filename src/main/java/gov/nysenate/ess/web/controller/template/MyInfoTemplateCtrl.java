package gov.nysenate.ess.web.controller.template;

import gov.nysenate.ess.core.model.auth.SimpleEssPermission;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.Permission;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * Handles requests for front-end templates associated with my info functionality.
 */
@Controller
@RequestMapping(MyInfoTemplateCtrl.MYINFO_TMPL_BASE_URL)
public class MyInfoTemplateCtrl extends BaseTemplateCtrl
{
    private static final Logger logger = LoggerFactory.getLogger(MyInfoTemplateCtrl.class);
    static final String MYINFO_TMPL_BASE_URL = TMPL_BASE_URL + "/myinfo";

    /**
     * Just return the corresponding template...
     * Unless the template uri is caught by one of the methods below.
     * @param request HttpServletRequest
     * @return String - passed in uri
     */
    @RequestMapping(value = "/**")
    public String getMyinfoPage(HttpServletRequest request) {
        return request.getRequestURI();
    }

    /**
     * Acknowledgment Report Pages
     *
     * Returns the given page if the currently authenticated user is permitted to view ack. report pages.
     * Otherwise return an error page indicating that the user does not have required permission.
     * @param request HttpServletRequest - the request (used to extract page uri)
     * @return String - passed in uri or error page depending on permissions
     */
    @RequestMapping(value = {
            "/personnel/ack-doc-report",
            "/personnel/emp-ack-doc-report"
    })
    public String getAcknowledgmentReportPage(HttpServletRequest request, ModelMap modelMap) {
        final Permission managementPermission = SimpleEssPermission.COMPLIANCE_REPORT_GENERATION.getPermission();
        if (SecurityUtils.getSubject().isPermitted(managementPermission)) {
            return request.getRequestURI();
        }
        modelMap.addAttribute("level", "error")
                .addAttribute("title", "This page is only available to Personnel");
        return SIMPLE_MESSAGE_URI;
    }
}
