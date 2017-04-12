package gov.nysenate.ess.core.service.security.authorization;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import gov.nysenate.ess.core.model.auth.CorePermission;
import gov.nysenate.ess.core.model.auth.EssRole;
import gov.nysenate.ess.core.model.auth.SimpleEssPermission;
import gov.nysenate.ess.core.model.personnel.Employee;
import org.apache.shiro.authz.Permission;
import org.springframework.stereotype.Component;

/**
 * Grants an employee permissions to access employee specific core ess data
 */
@Component
public class EmployeePermissionFactory implements PermissionFactory {

    /** {@inheritDoc} */
    @Override
    public ImmutableList<Permission> getPermissions(Employee employee, ImmutableSet<EssRole> roles) {
        return ImmutableList.of(
                SimpleEssPermission.SENATE_EMPLOYEE.getPermission(),
                new CorePermission(employee.getEmployeeId())
        );
    }
}
