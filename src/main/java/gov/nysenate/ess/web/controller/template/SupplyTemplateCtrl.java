package gov.nysenate.ess.web.controller.template;

import gov.nysenate.ess.supply.authorization.permission.SupplyPermission;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.permission.WildcardPermission;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping(SupplyTemplateCtrl.SUPPLY_TMPL_BASE_URL)
public class SupplyTemplateCtrl extends BaseTemplateCtrl
{
    private static final Logger logger = LoggerFactory.getLogger(SupplyTemplateCtrl.class);

    protected static final String SUPPLY_TMPL_BASE_URL = TMPL_BASE_URL + "/supply";
    private static final String NOT_SUPPLY_EMPLOYEE_PAGE = SUPPLY_TMPL_BASE_URL + "/error/not-supply-employee";

    /**
     * Generic mapping to handle all requests that don't require permission.
     * Assumes the request URI equals the location in the WEB_INF/view directory.
     */
    @RequestMapping(value="/**")
    public String supplyTemplate(HttpServletRequest request) {
        return request.getRequestURI();
    }

    /** --- Manage --- */

    @RequestMapping(value="/manage/fulfillment")
    public String manageOrder() {
        return getSupplyEmployeePage(SUPPLY_TMPL_BASE_URL + "/manage/fulfillment",
                SupplyPermission.SUPPLY_EMPLOYEE.getPermission());
    }

    /** --- History --- */

    @RequestMapping(value="/history/item/requisition-history")
    public String requisitionHistory() {
        return getSupplyEmployeePage(SUPPLY_TMPL_BASE_URL + "/history/item/requisition-history",
                SupplyPermission.SUPPLY_UI_MANAGE_REQUISITION_HISTORY.getPermission());
    }

    @RequestMapping(value="/history/item-history")
    public String itemHistory() {
        return getSupplyEmployeePage(SUPPLY_TMPL_BASE_URL + "/history/item/item-history",
                SupplyPermission.SUPPLY_UI_MANAGE_ITEM_HISTORY.getPermission());
    }

    @RequestMapping(value="/manage/reconciliation")
    public String reconciliation() {
        return getSupplyEmployeePage(SUPPLY_TMPL_BASE_URL + "/manage/reconciliation",
                SupplyPermission.SUPPLY_UI_MANAGE_RECONCILIATION.getPermission());
    }

    @RequestMapping(value="/manage/modal/fulfillment-editing-modal")
    public String managePendingModal() {
        return getSupplyEmployeePage(SUPPLY_TMPL_BASE_URL + "/manage/modal/fulfillment-editing-modal",
                SupplyPermission.SUPPLY_EMPLOYEE.getPermission());
    }

    @RequestMapping(value="/manage/modal/fulfillment-immutable-modal")
    public String manageCompletedModal() {
        return getSupplyEmployeePage(SUPPLY_TMPL_BASE_URL + "/manage/modal/fulfillment-immutable-modal",
                SupplyPermission.SUPPLY_EMPLOYEE.getPermission());
    }

    @RequestMapping(value="/manage/modal/editable-order-listing")
    public String editableOrderListing() {
        return getSupplyEmployeePage(SUPPLY_TMPL_BASE_URL + "/manage/modal/editable-order-listing",
                SupplyPermission.SUPPLY_EMPLOYEE.getPermission());
    }

    private String getSupplyEmployeePage(String pageName, WildcardPermission requiredPermission) {
        if (SecurityUtils.getSubject().isPermitted(requiredPermission)) {
            return pageName;
        }
        return NOT_SUPPLY_EMPLOYEE_PAGE;
    }

}
