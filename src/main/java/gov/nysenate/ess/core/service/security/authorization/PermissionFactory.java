package gov.nysenate.ess.core.service.security.authorization;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import gov.nysenate.ess.core.model.auth.EssRole;
import gov.nysenate.ess.core.model.personnel.Employee;
import org.apache.shiro.authz.Permission;

/**
 * Contains logic to assign permissions to an employee.
 * Typically, each permission factory implementation grants permissions based on a single role or domain.
 */
public interface PermissionFactory {

    /**
     * Returns granted permissions as defined under this permission factory
     *
     * As there will be multiple {@link PermissionFactory} implementations,
     * this list will only include a subset of the employee's permissions
     *
     * @param employee {@link Employee} - an employee object for the authenticated user
     * @param roles {@link ImmutableSet<EssRole>} - a set of roles assigned to the authenticated user
     * @return {@link ImmutableList<Permission>} - a list of permissions granted
     */
    ImmutableList<Permission> getPermissions(Employee employee, ImmutableSet<EssRole> roles);
}
