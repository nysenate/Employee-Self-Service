package gov.nysenate.ess.web.controller.template;

import gov.nysenate.ess.time.model.auth.SimpleTimePermission;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.Permission;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

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

    /** --- Record Pages --- */

    @RequestMapping(value="/record/entry")
    public String entry() {
        return TIME_TMPL_BASE_URL + "/record/entry";
    }

    @RequestMapping(value="/record/history")
    public String history() {
        return TIME_TMPL_BASE_URL + "/record/history";
    }

    /** --- Record Templates --- */

    @RequestMapping(value="/record/details")
    public String recordDetails() {
        return TIME_TMPL_BASE_URL + "/record/details";
    }


    /** --- Supervisor Pages ---
     *
     * For these pages, if the requester is not a supervisor, they are served an error page
     */

    @RequestMapping(value="/record/manage")
    public String manage() {
        return getManagementPage(TIME_TMPL_BASE_URL + "/record/manage");
    }

    @RequestMapping(value="/record/emphistory")
    public String employeeHistory() {
        return getManagementPage(TIME_TMPL_BASE_URL + "/record/emp-history");
    }

    @RequestMapping(value="/record/grant")
    public String grant() {
        return getManagementPage(TIME_TMPL_BASE_URL + "/record/grant");
    }

    /** --- Supervisor Templates --- */

    @RequestMapping(value = "/record/supervisor-record-list")
    public String recordList() {
        return TIME_TMPL_BASE_URL + "/record/supervisor-record-list";
    }

    @RequestMapping(value = "/record/record-review-modal")
    public String recordReviewModal() {
        return TIME_TMPL_BASE_URL + "/record/record-review-modal";
    }

    @RequestMapping(value = "/record/record-reject-modal")
    public String recordRejectModal() {
        return TIME_TMPL_BASE_URL + "/record/record-reject-modal";
    }

    @RequestMapping(value = "/record/record-approve-submit-modal")
    public String recordApproveSubmitModal() {
        return TIME_TMPL_BASE_URL + "/record/record-approve-submit-modal";
    }

    @RequestMapping(value = "/record/record-reminder-modal")
    public String recordReminderModal() {
        return TIME_TMPL_BASE_URL + "/record/record-reminder-modal";
    }

    /** --- Accruals --- */

    @RequestMapping(value="/accrual/history")
    public String accrualHistory() {
        return TIME_TMPL_BASE_URL + "/accrual/history";
    }

    @RequestMapping(value="/accrual/projections")
    public String accrualProjections() {
        return TIME_TMPL_BASE_URL + "/accrual/projections";
    }

    /** --- Calendar --- */

    @RequestMapping(value="/period/calendar")
    public String payPeriodView() {
        return TIME_TMPL_BASE_URL + "/period/calendar";
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