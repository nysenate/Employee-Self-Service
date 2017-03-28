package gov.nysenate.ess.web.controller.template;

import gov.nysenate.ess.time.model.auth.SimpleTimePermission;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.Permission;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * Handles requests for front-end templates associated with time/attendance functionality.
 */
@Controller
@RequestMapping(TimeTemplateCtrl.TIME_TMPL_BASE_URL)
public class TimeTemplateCtrl extends BaseTemplateCtrl
{
    private static final Logger logger = LoggerFactory.getLogger(TimeTemplateCtrl.class);
    static final String TIME_TMPL_BASE_URL = TMPL_BASE_URL + "/time";

    private static final Permission MANAGE_PAGE_PERMISSION =
            SimpleTimePermission.MANAGEMENT_PAGES.getPermission();

    private static final String NOT_A_SUPERVISOR_PAGE = TIME_TMPL_BASE_URL + "/error/not-supervisor";

    @RequestMapping(value = "/**")
    public String getTimePage(HttpServletRequest request) {
        return request.getRequestURI();
    }

    /** --- Supervisor Pages ---
     *
     * For these pages, if the requester is not a supervisor, they are served an error page
     */

    @RequestMapping(value="/record/manage")
    public String manage() {
        return getManagementPage(TIME_TMPL_BASE_URL + "/record/manage");
    }

    @RequestMapping(value="/record/emp-history")
    public String employeeHistory() {
        return getManagementPage(TIME_TMPL_BASE_URL + "/record/emp-history");
    }

    @RequestMapping(value="/record/grant")
    public String grant() {
        return getManagementPage(TIME_TMPL_BASE_URL + "/record/grant");
    }

    @RequestMapping(value = "/accrual/emp-history")
    public String accrualEmpHistory() {
        return getManagementPage(TIME_TMPL_BASE_URL + "/accrual/emp-history");
    }

    @RequestMapping(value = "/accrual/emp-projections")
    public String accrualEmpProjections() {
        return getManagementPage(TIME_TMPL_BASE_URL + "/accrual/emp-projections");
    }

    /** --- Internal Methods --- */

    /**
     * Returns the given page if the currently authenticated user is permitted to view management pages
     * Otherwise return an error page indicating that the user does not have required permission
     * @param pageName String - name of the requested management page
     * @return String - passed in <code>pageName</code> or error page depending on permissions
     */
    private String getManagementPage(String pageName) {
        if (SecurityUtils.getSubject().isPermitted(MANAGE_PAGE_PERMISSION)) {
            return pageName;
        }
        return NOT_A_SUPERVISOR_PAGE;
    }
}