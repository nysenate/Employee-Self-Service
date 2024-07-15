package gov.nysenate.ess.web.controller.page;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/supply/**")
public class SupplyPageCtrl extends BaseEssPageCtrl
{
    private static final Logger logger = LoggerFactory.getLogger(SupplyPageCtrl.class);

    @RequestMapping(method = {RequestMethod.GET, RequestMethod.HEAD})
    @Override
    String mainPage(ModelMap modelMap, HttpServletRequest request) {
        addCommonModelMapData(modelMap);
        return "supply";
    }
}
