package gov.nysenate.ess.web.controller.template;

/**
 * Base class for template controllers to extend.
 * The explicit mappings are put in place for security reasons.
 */
public class BaseTemplateCtrl
{
    protected static final String TMPL_BASE_URL = "/template";

    protected static final String SIMPLE_MESSAGE_URI = TMPL_BASE_URL + "/common/error/simple-message";
}
