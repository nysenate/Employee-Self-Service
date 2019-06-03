package gov.nysenate.ess.travel.authorization.permission;

import org.apache.shiro.authz.permission.WildcardPermission;

public enum TravelPermission {

    // Allowed to view Manage sub section in navigation
    TRAVEL_UI_MANAGE("travel:ui:manage"),
    // Allowed to browse the application review page
    TRAVEL_UI_REVIEW("travel:ui:review"),
    // Allowed to browse the application review history page
    TRAVEL_UI_REVIEW_HISTORY("travel:ui:review-history"),
    // Allowed to edit at least 1 application. The patch api will check edit permissions at a more granular level.
    // This is necessary because template and JSP permissions are checked on the back end before
    // we know which app is attempting to be edited.
    // The side effect of this is someone could potentially be viewing the edit page of an application they
    // are not allowed to edit (and the api will block them if they try to edit it). But this should never
    // happen through natural use of the UI.
    TRAVEL_UI_EDIT_APP("travel:ui:edit-app"),
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
