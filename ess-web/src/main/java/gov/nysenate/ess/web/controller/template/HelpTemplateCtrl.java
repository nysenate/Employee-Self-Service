package gov.nysenate.ess.web.controller.template;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Handles requests for front-end templates associated with help/documentation.
 */
@Controller
@RequestMapping(HelpTemplateCtrl.HELP_TMPL_BASE_URL)
public class HelpTemplateCtrl extends BaseTemplateCtrl
{
    private static final Logger logger = LoggerFactory.getLogger(HelpTemplateCtrl.class);
    protected static final String HELP_TMPL_BASE_URL = TMPL_BASE_URL + "/help";

    /** --- T&A --- */

    @RequestMapping(value="/ta/plan")
    public String taPlan() {
        return HELP_TMPL_BASE_URL + "/ta/plan";
    }

}
