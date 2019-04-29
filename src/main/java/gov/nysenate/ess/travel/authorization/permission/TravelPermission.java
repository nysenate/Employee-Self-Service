package gov.nysenate.ess.travel.authorization.permission;

import org.apache.shiro.authz.permission.WildcardPermission;

public enum TravelPermission {

    // Allows user to access ui pages for approving applications.
    TRAVEL_UI_REVIEW("travel:ui:approval")
    ;

    private String permissionString;

    TravelPermission(String permissionString) {
        this.permissionString = permissionString;
    }

    public String getPermissionString() {
        return permissionString;
    }

    public WildcardPermission getPermission() {
        return new WildcardPermission(getPermissionString());
    }
}
