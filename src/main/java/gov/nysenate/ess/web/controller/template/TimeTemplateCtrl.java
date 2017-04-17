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

    private static final String NOT_A_SUPERVISOR_PAGE = TIME_TMPL_BASE_URL + "/error/not-supervisor";

    private static final String NO_TIME_ENTRY_PAGE = TIME_TMPL_BASE_URL + "/error/no-time-entry";

    private static final String NO_ACCRUALS_PAGE = TIME_TMPL_BASE_URL + "/error/no-accruals";

    /**
     * Return the corresponding template...
     * Unless the template uri is caught by one of the methods below
     * @param request HttpServletRequest
     * @return String - passed in uri
     */
    @RequestMapping(value = "/**")
    public String getTimePage(HttpServletRequest request) {
        return request.getRequestURI();
    }

    /**
     * Supervisor Pages
     *
     * Returns the given page if the currently authenticated user is permitted to view management pages
     * Otherwise return an error page indicating that the user does not have required permission
     * @param request HttpServletRequest - the request (used to extract page uri)
     * @return String - passed in uri or error page depending on permissions
     */
    @RequestMapping(value = {
            "/record/manage",
            "/record/emp-history",
            "/record/grant",
            "/accrual/emp-history",
            "/accrual/emp-projections"
    })
    public String getManagementPage(HttpServletRequest request) {
        final Permission managementPermission =
                SimpleTimePermission.MANAGEMENT_PAGES.getPermission();
        if (SecurityUtils.getSubject().isPermitted(managementPermission)) {
            return request.getRequestURI();
        }
        return NOT_A_SUPERVISOR_PAGE;
    }

    /**
     * Attendance Entry Pages
     *
     * Returns the given page if the currently authenticated user is permitted to view attendance entry pages
     * Otherwise return an error page indicating that the user does not have required permission
     * @param request HttpServletRequest - the request (used to extract page uri)
     * @return String - passed in uri or error page depending on permissions
     */
    @RequestMapping(value = {
            "/record/entry",
            "/record/history"
    })
    public String getMyAttendancePage(HttpServletRequest request) {
        final Permission attendRecordPermission = SimpleTimePermission.ATTENDANCE_RECORD_PAGES.getPermission();
        if (SecurityUtils.getSubject().isPermitted(attendRecordPermission)) {
            return request.getRequestURI();
        }
        return NO_TIME_ENTRY_PAGE;
    }

    /**
     * Accrual Pages
     *
     * Returns the given page if the currently authenticated user is permitted to view or project accruals
     * Otherwise return an error page indicating that the user does not have required permission
     * @param request HttpServletRequest - the request (used to extract page uri)
     * @return String - passed in uri or error page depending on permissions
     */
    @RequestMapping(value = {
            "/accrual/history",
            "/accrual/projections"
    })
    public String getMyAccrualPage(HttpServletRequest request) {
        final Permission accrualPagePermission = SimpleTimePermission.ACCRUAL_PAGES.getPermission();
        if (SecurityUtils.getSubject().isPermitted(accrualPagePermission)) {
            return request.getRequestURI();
        }
        return NO_TIME_ENTRY_PAGE;
    }
}