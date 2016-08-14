package gov.nysenate.ess.time.service.auth;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import gov.nysenate.ess.core.model.auth.EssRole;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.service.auth.PermissionFactory;
import gov.nysenate.ess.time.model.auth.EssTimePermission;
import org.apache.shiro.authz.Permission;
import org.springframework.stereotype.Component;

/**
 * Grants an employee permissions to access employee specific ess time data
 */
@Component
public class EssTimeEmployeePermissionFactory implements PermissionFactory {

    /** {@inheritDoc} */
    @Override
    public ImmutableList<Permission> getPermissions(Employee employee, ImmutableSet<EssRole> roles) {
        return ImmutableList.of(new EssTimePermission(employee.getEmployeeId()));
    }
}
