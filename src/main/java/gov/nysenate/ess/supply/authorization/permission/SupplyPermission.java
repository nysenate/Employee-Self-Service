package gov.nysenate.ess.supply.authorization.permission;

import org.apache.shiro.authz.permission.WildcardPermission;

public enum SupplyPermission {

    SUPPLY_EMPLOYEE("supply:employee"),
    SUPPLY_REQUISITION_APPROVE("supply:requisition:approve")
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
