package gov.nysenate.ess.time.model.auth;

import org.apache.shiro.authz.permission.WildcardPermission;


/**
 * Enumerates ess time permissions that do not need to be generated in a dynamic way
 */
public enum SimpleTimePermission {

    /** Granted to supervisors allowing them to use the time management pages */
    MANAGEMENT_PAGES("time:management-pages"),

    /** Granted to annual employees allowing them to view accrual projections */
    ACCRUAL_PROJECTIONS("time:accrual-projections:view")
    ;

    private String permissionString;

    SimpleTimePermission(String permissionString) {
        this.permissionString = permissionString;
    }

    public String getPermissionString() {
        return permissionString;
    }

    public WildcardPermission getPermission() {
        return new WildcardPermission(permissionString);
    }
}
