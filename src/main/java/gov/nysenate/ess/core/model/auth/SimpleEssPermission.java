package gov.nysenate.ess.core.model.auth;

import org.apache.shiro.authz.permission.WildcardPermission;

/**
 * Enumerates permissions that do not need to be generated in a dynamic way
 */
public enum SimpleEssPermission {

    /** Granted to all users that log in, used to ensure that authorization worked */
    SENATE_EMPLOYEE("core:senate-employee"),

    /** Granted to users belonging to departments that are granted access to the app */
    DEPARTMENT_ACCESS("core:department-access"),

    /** Granted to users who should not time out when using the front end application */
    TIMEOUT_EXEMPT("core:timeout-exempt"),

    /**Granted to users who should be able to generate out acknowledgment reports */
    ACK_REPORT_GENERATION("core;ack-report-generation"),

    /** Granted to select personnel employees, allowing them to view complete employee data */
    PERSONNEL_PAGES("core:personnel-pages"),
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
