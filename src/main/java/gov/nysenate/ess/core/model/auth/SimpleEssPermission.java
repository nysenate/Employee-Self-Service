package gov.nysenate.ess.core.model.auth;

import org.apache.shiro.authz.permission.WildcardPermission;

/**
 * Enumerates permissions that do not need to be generated in a dynamic way
 */
public enum SimpleEssPermission {

    /** Granted to users belonging to departments that are granted access to the app */
    DEPARTMENT_ACCESS("core:department-access"),

    /** Granted to users who should not time out when using the front end application */
    TIMEOUT_EXEMPT("core:timeout-exempt"),
    ;

    private String permissionString;

    SimpleEssPermission(String permissionString) {
        this.permissionString = permissionString;
    }

    public String getPermissionString() {
        return permissionString;
    }

    public WildcardPermission getPermission() {
        return new WildcardPermission(permissionString);
    }
}
