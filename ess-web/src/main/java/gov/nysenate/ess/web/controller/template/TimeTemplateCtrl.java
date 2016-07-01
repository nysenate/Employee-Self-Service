package gov.nysenate.ess.web.controller.template;

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
    protected static final String TIME_TMPL_BASE_URL = TMPL_BASE_URL + "/time";

    /** --- Records --- */

    @RequestMapping(value="/record/entry")
    public String entry() {
        return TIME_TMPL_BASE_URL + "/record/entry";
    }

    @RequestMapping(value="/record/history")
    public String history() {
        return TIME_TMPL_BASE_URL + "/record/history";
    }

    @RequestMapping(value="/record/details")
    public String recordDetails() {
        return TIME_TMPL_BASE_URL + "/record/details";
    }


    /** --- Supervisor Records --- */

    @RequestMapping(value="/record/manage")
    public String manage() {
        return TIME_TMPL_BASE_URL + "/record/manage";
    }

    @RequestMapping(value="/record/emphistory")
    public String employeeHistory() {
        return TIME_TMPL_BASE_URL + "/record/emp-history";
    }

    @RequestMapping(value="/record/grant")
    public String grant() {
        return TIME_TMPL_BASE_URL + "/record/grant";
    }

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
}