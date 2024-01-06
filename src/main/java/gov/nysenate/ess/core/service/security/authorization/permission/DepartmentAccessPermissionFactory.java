package gov.nysenate.ess.core.service.security.authorization.permission;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import gov.nysenate.ess.core.model.auth.SimpleEssPermission;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.service.security.authorization.DepartmentalWhitelistService;
import org.apache.shiro.authz.Permission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * A {@link PermissionFactory} that grants a specific permission ({@link SimpleEssPermission#DEPARTMENT_ACCESS})
 * if the user's department is granted access to the ESS app
 *
 * @see DepartmentalWhitelistService
 */
@Service
public class DepartmentAccessPermissionFactory implements PermissionFactory {

    @Autowired private DepartmentalWhitelistService deptWhitelistService;

    /** {@inheritDoc} */
    @Override
    public ImmutableList<Permission> getPermissions(Employee employee, ImmutableSet<Enum<?>> roles) {
        return deptWhitelistService.isAllowed(employee)
                ? ImmutableList.of(SimpleEssPermission.DEPARTMENT_ACCESS.getPermission())
                : ImmutableList.of();
    }
}
