package gov.nysenate.ess.core.service.security.authorization;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import gov.nysenate.ess.core.model.auth.EssRole;
import gov.nysenate.ess.core.model.personnel.Employee;
import org.apache.shiro.authz.Permission;

public interface PermissionFactory {

    /**
     * Defines an employee's permissions for a single Ess application.
     *
     * Each app has its own implementation and a single employee can get
     * permissions from multiple implementations.
     *
     * An employee's overall permissions in Ess is the sum of their permissions from
     * all implementations of this class.
     */
    ImmutableList<Permission> getPermissions(Employee employee, ImmutableSet<EssRole> roles);
}
