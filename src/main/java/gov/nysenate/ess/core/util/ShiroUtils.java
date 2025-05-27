package gov.nysenate.ess.core.util;

import gov.nysenate.ess.core.model.auth.SenatePerson;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.UnauthenticatedException;
import org.apache.shiro.subject.Subject;

/**
 * Utilities related to use of ApacheShiro in ESS
 */
public abstract class ShiroUtils {

    /**
     * Gets the currently logged in user
     * @return {@link SenatePerson} the currently logged in person
     */
    public static SenatePerson getAuthenticatedUser() {
        Subject subject = SecurityUtils.getSubject();
        if (subject.isAuthenticated()) {
            return (SenatePerson) subject.getPrincipal();
        }
        throw new UnauthenticatedException("User has not been authenticated.");
    }

    /**
     * Get the employee id of the currently logged in user
     * @return int - the employee id of the currently logged in user
     */
    public static int getAuthenticatedEmpId() {
        return getAuthenticatedUser().getEmployeeId();
    }

    public static String getAuthenticatedUid(){return getAuthenticatedUser().getUid();}
}
