package gov.nysenate.ess.web.controller.template;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
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
    protected static final String MYINFO_TMPL_BASE_URL = TMPL_BASE_URL + "/myinfo";

    /**
     * Just return the corresponding template...
     *
     * @param request HttpServletRequest
     * @return String - passed in uri
     */
    @RequestMapping(value = "/**")
    public String getMyinfoPage(HttpServletRequest request) {
        return request.getRequestURI();
    }
}
