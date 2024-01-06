package gov.nysenate.ess.core.model.auth;

import org.apache.shiro.authz.permission.WildcardPermission;

/**
 * Enumerates permissions that do not need to be generated in a dynamic way
 */
public enum SimpleEssPermission {

    /** Granted to all admin users. */
    ADMIN("admin"),

    /** Granted to all users that log in, used to ensure that authorization worked */
    SENATE_EMPLOYEE("core:senate-employee"),

    /** Granted to users belonging to departments that are granted access to the app */
    DEPARTMENT_ACCESS("core:department-access"),

    /** Granted to users who should not time out when using the front end application */
    TIMEOUT_EXEMPT("core:timeout-exempt"),

    /**Granted to users who should be able to generate out acknowledgment reports */
    COMPLIANCE_REPORT_GENERATION("core:pec-report-generation"),

    /** Allows user to manually run personnel task assigner */
    RUN_PERSONNEL_TASK_ASSIGNER("admin:personnel-task-assigner"),
    ;

    private final String permissionString;

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
