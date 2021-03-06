package gov.nysenate.ess.web.controller.template;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping(BaseTemplateCtrl.TMPL_BASE_URL)
public class CommonTemplateCtrl extends BaseTemplateCtrl
{

    @RequestMapping("/404")
    public String pageNotFound() {
        return TMPL_BASE_URL + "/base/404";
    }

    @RequestMapping("/common/**")
    public String getCommonTemplate(HttpServletRequest request) {
        return request.getRequestURI();
    }
}
