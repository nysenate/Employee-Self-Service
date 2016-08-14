package gov.nysenate.ess.core.model.auth;

import org.apache.shiro.authz.permission.WildcardPermission;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Extension of {@link WildcardPermission} that constructs permissions in a standardized format
 * The format for employee data permissions are as follows:
 *      core:employee-{employee id}:{affected object}:{restful action}
 */
public class CorePermission extends WildcardPermission {

    private static final String CORE_DOMAIN = "core";
    private static final String EMP_ID_PART_PREFIX = "employee-";

    /**
     * Grants all permissions relating to the given employee
     * @param empId int employee id
     */
    public CorePermission(int empId) {
        super(getEmployeePart(empId));
    }

    /**
     * Grants permissions to perform an action on a specific object belonging to the given employee
     * @param empId int - employee id
     * @param object CorePermissionObject - object
     * @param action RequestMethod - action
     */
    public CorePermission(int empId, CorePermissionObject object, RequestMethod action) {
        super(getPermissionString(empId, object, action));
    }

    private static String getEmployeePart(int empId) {
        return CORE_DOMAIN + PART_DIVIDER_TOKEN
                + EMP_ID_PART_PREFIX + empId;
    }

    private static String getPermissionString(int empId, CorePermissionObject object, RequestMethod action) {
        return getEmployeePart(empId) + PART_DIVIDER_TOKEN +
                object + PART_DIVIDER_TOKEN +
                action;
    }
}
