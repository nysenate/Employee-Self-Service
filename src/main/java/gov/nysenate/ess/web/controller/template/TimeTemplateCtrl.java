package gov.nysenate.ess.web.controller.template;

import gov.nysenate.ess.time.model.auth.SimpleTimePermission;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.Permission;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
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

    private static final String TIME_MESSAGE_URI = TIME_TMPL_BASE_URL + "/error/time-message";

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
    public String getManagementPage(HttpServletRequest request, ModelMap modelMap) {
        final Permission managementPermission =
                SimpleTimePermission.MANAGEMENT_PAGES.getPermission();
        if (SecurityUtils.getSubject().isPermitted(managementPermission)) {
            return request.getRequestURI();
        }
        modelMap.addAttribute("level", "error")
                .addAttribute("title", "This page is only available for Time and Attendance Supervisors");
        return TIME_MESSAGE_URI;
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
    public String getMyAttendancePage(HttpServletRequest request, ModelMap modelMap) {
        final Permission attendRecordPermission = SimpleTimePermission.ATTENDANCE_RECORD_PAGES.getPermission();
        if (SecurityUtils.getSubject().isPermitted(attendRecordPermission)) {
            return request.getRequestURI();
        }
        modelMap.addAttribute("level", "info")
                .addAttribute("title", "Time Entry Not Required")
                .addAttribute("message", "You are not required to submit attendance records in ESS.");
        return TIME_MESSAGE_URI;
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
    public String getMyAccrualPage(HttpServletRequest request, ModelMap modelMap) {
        final Permission accrualPagePermission = SimpleTimePermission.ACCRUAL_PAGES.getPermission();
        if (SecurityUtils.getSubject().isPermitted(accrualPagePermission)) {
            return request.getRequestURI();
        }
        modelMap.addAttribute("level", "info")
                .addAttribute("title", "Time Entry Not Required")
                .addAttribute("message", "You are not required to submit attendance records in ESS.");
        return TIME_MESSAGE_URI;
    }

    /**
     * Allowance Page
     *
     * Tests that the user is able to view the allowance page.
     * @param request HttpServletRequest
     * @param modelMap ModelMap
     * @return String - passed in uri or error page depending on permissions
     */
    @RequestMapping(value = "/allowance/status")
    public String getAllowanceStatusPage(HttpServletRequest request, ModelMap modelMap) {
        final Permission allowancePagePermission = SimpleTimePermission.ALLOWANCE_PAGE.getPermission();
        if (SecurityUtils.getSubject().isPermitted(allowancePagePermission)) {
            return request.getRequestURI();
        }
        modelMap.addAttribute("level", "error")
                .addAttribute("title", "Allowance Not Available")
                .addAttribute("message", "You are not permitted to view allowance status.");
        return TIME_MESSAGE_URI;
    }

    /**
     * Employee Allowance Page
     *
     * Tests that the user is able to view the employee allowance page.
     * @param request HttpServletRequest
     * @param modelMap ModelMap
     * @return String - passed in uri or error page depending on permissions
     */
    @RequestMapping(value = "/allowance/emp-status")
    public String getAllowanceEmpStatusPage(HttpServletRequest request, ModelMap modelMap) {
        final Permission allowancePagePermission = SimpleTimePermission.EMPLOYEE_ALLOWANCE_PAGE.getPermission();
        // First run through the management page permission
        String uri = getManagementPage(request, modelMap);
        if (TIME_MESSAGE_URI.equals(uri)) {
            return uri;
        }
        if (SecurityUtils.getSubject().isPermitted(allowancePagePermission)) {
            return getManagementPage(request, modelMap);
        }
        // Also require management page permission check
        modelMap.addAttribute("level", "error")
                .addAttribute("title", "Employee Allowance Not Available")
                .addAttribute("message",
                        "You are not permitted to view employee allowance status.");
        return TIME_MESSAGE_URI;
    }

    /**
     * Personnel Pages
     *
     * Tests that the user is able to view personnel pages
     *
     * @param request HttpServletRequest
     * @param modelMap ModelMap
     * @return String - passed in uri or error page depending on permissions
     */
    @RequestMapping(value = "/personnel/search")
    public String getPersonnelPage(HttpServletRequest request, ModelMap modelMap) {
        final Permission personnelPagePermission = SimpleTimePermission.PERSONNEL_PAGES.getPermission();
        if (SecurityUtils.getSubject().isPermitted(personnelPagePermission)) {
            return request.getRequestURI();
        }
        // Also require management page permission check
        modelMap.addAttribute("level", "error")
                .addAttribute("title", "This page is only available to Personnel");
        return TIME_MESSAGE_URI;
    }
}