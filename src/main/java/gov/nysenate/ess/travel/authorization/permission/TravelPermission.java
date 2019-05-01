package gov.nysenate.ess.travel.authorization.permission;

import org.apache.shiro.authz.permission.WildcardPermission;

public enum TravelPermission {

    TRAVEL_UI_MANAGE("travel:ui:manage"),                   // Allowed to view Manage sub section in navigation
    TRAVEL_UI_REVIEW("travel:ui:review"),                   // Allowed to browse the application review page
    TRAVEL_UI_REVIEW_HISTORY("travel:ui:review-history")    // Allowed to browse the application review history page
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
