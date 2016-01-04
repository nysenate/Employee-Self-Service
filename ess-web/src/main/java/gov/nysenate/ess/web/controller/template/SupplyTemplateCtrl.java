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

    @RequestMapping(value="/location/history")
    public String locationHistory() {
        return SUPPLY_TMPL_BASE_URL + "/requisition/location-history";
    }

    @RequestMapping(value="/requisition/order")
    public String supplyOrder() {
        return SUPPLY_TMPL_BASE_URL + "/requisition/order";
    }

    @RequestMapping(value="/requisition/manage")
    public String manageOrder() {
        return SUPPLY_TMPL_BASE_URL + "/requisition/manage";
    }

    @RequestMapping(value="/requisition/manage/editing/modal")
    public String managePendingModal() {
        return SUPPLY_TMPL_BASE_URL + "/requisition/manage-editing-modal";
    }

    @RequestMapping(value="/requisition/manage/completed/modal")
    public String manageCompletedModal() {
        return SUPPLY_TMPL_BASE_URL + "/requisition/manage-completed-modal";
    }

    @RequestMapping(value="/requisition/editable/order/listing")
    public String editableOrderListing() {
        return SUPPLY_TMPL_BASE_URL + "/requisition/editable-order-listing";
    }

    @RequestMapping(value="/requisition/view")
    public String viewOrder() {
        return SUPPLY_TMPL_BASE_URL + "/requisition/view";
    }

    @RequestMapping(value="/requisition/history")
    public String orderHistory() {
        return SUPPLY_TMPL_BASE_URL + "/requisition/history";
    }

    @RequestMapping(value="/cart/cart-summary")
    public String cartSummary() {
        return SUPPLY_TMPL_BASE_URL + "/cart/cart-summary";
    }

    @RequestMapping(value="/cart/cart")
    public String cart() {
        return SUPPLY_TMPL_BASE_URL + "/cart/cart";
    }

    @RequestMapping(value="/reconciliation/reconciliation")
    public String reconciliation() {
        return SUPPLY_TMPL_BASE_URL + "/reconciliation/reconciliation";
    }
}
