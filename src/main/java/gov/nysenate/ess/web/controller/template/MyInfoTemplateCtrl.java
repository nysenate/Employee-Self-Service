package gov.nysenate.ess.web.controller.template;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Handles requests for front-end templates associated with my info functionality.
 */
@Controller
@RequestMapping(MyInfoTemplateCtrl.MYINFO_TMPL_BASE_URL)
public class MyInfoTemplateCtrl extends BaseTemplateCtrl
{
    private static final Logger logger = LoggerFactory.getLogger(MyInfoTemplateCtrl.class);
    protected static final String MYINFO_TMPL_BASE_URL = TMPL_BASE_URL + "/myinfo";

    /** --- Personnel --- */

    @RequestMapping(value="/personnel/summary")
    public String profileSummary() {
        return MYINFO_TMPL_BASE_URL + "/personnel/summary";
    }

    @RequestMapping(value="/personnel/transactions")
    public String personnelTransactions() {
        return MYINFO_TMPL_BASE_URL + "/personnel/transactions";
    }

    @RequestMapping(value="/payroll/checkhistory")
    public String checkHistory() {
        return MYINFO_TMPL_BASE_URL + "/payroll/checkhistory";
    }
}
