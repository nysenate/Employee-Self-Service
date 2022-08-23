package gov.nysenate.ess.web.controller.page;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/supply/**")
public class SupplyPageCtrl
{
    private static final Logger logger = LoggerFactory.getLogger(SupplyPageCtrl.class);

    final PageCtrlUtils pageCtrlUtils;

    @Autowired
    public SupplyPageCtrl(PageCtrlUtils pageCtrlUtils) {
        this.pageCtrlUtils = pageCtrlUtils;
    }

    @RequestMapping(method = {RequestMethod.GET, RequestMethod.HEAD})
    public Object supplyPage(ModelMap modelMap, HttpServletRequest request) {
        modelMap.addAllAttributes(pageCtrlUtils.commonPageData());
        return "supply";
    }
}
