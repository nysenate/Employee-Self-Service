package gov.nysenate.ess.supply.authorization.permission;

import org.apache.shiro.authz.permission.WildcardPermission;

public enum SupplyPermission {

    SUPPLY_EMPLOYEE("supply:employee"),
    SUPPLY_STAFF_VIEW("supply:staff:view"), // Permission to query supply employee info api.
    SUPPLY_REQUISITION_APPROVE("supply:requisition:approve"),

    // UI Permissions - Permissions to see various supply management pages.
    SUPPLY_UI_NAV_MANAGE("supply:ui:nav:manage"), // Permission to view the Manage sub list of links on the navigation bar.

    SUPPLY_UI_MANAGE("supply:ui:manage"), // Permission to view ALL supply manage pages.
    SUPPLY_UI_MANAGE_FULFILLMENT("supply:ui:manage:fulfillment"), // Permission to view the fulfillment page.
    SUPPLY_UI_MANAGE_RECONCILIATION("supply:ui:manage:reconciliation"), // Permission to view the reconciliation page.
    SUPPLY_UI_MANAGE_REQUISITION_HISTORY("supply:ui:manage:requisition-history"), // Permission to view the requisition history page.
    SUPPLY_UI_MANAGE_ITEM_HISTORY("supply:ui:manage:item-history") // Permission to view the item history page.
    ;

    private String permissionString;

    SupplyPermission(String permissionString) {
        this.permissionString = permissionString;
    }

    public String getPermissionString() {
        return permissionString;
    }

    public WildcardPermission getPermission() {
        return new WildcardPermission(getPermissionString());
    }
}
