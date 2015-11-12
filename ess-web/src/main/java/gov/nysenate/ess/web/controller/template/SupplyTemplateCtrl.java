package gov.nysenate.ess.web.controller.template;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(SupplyTemplateCtrl.SUPPLY_TMPL_BASE_URL)
public class SupplyTemplateCtrl extends BaseTemplateCtrl
{
    private static final Logger logger = LoggerFactory.getLogger(SupplyTemplateCtrl.class);
    protected static final String SUPPLY_TMPL_BASE_URL = TMPL_BASE_URL + "/supply";

    @RequestMapping(value="/requisition/order")
    public String supplyOrder() {
        return SUPPLY_TMPL_BASE_URL + "/requisition/order";
    }

    @RequestMapping(value="/requisition/categories")
    public String categoryNavigation() {
        return SUPPLY_TMPL_BASE_URL + "/requisition/category-navigation";
    }

    @RequestMapping(value="/cart/cart-summary")
    public String cartSummary() {
        return SUPPLY_TMPL_BASE_URL + "/cart/cart-summary";
    }

    @RequestMapping(value="/cart/cart")
    public String cart() {
        return SUPPLY_TMPL_BASE_URL + "/cart/cart";
    }
}
