package gov.nysenate.ess.supply.permission;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import gov.nysenate.ess.core.model.auth.EssRole;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.service.auth.PermissionFactory;
import org.apache.shiro.authz.Permission;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class SupplyPermissionFactory implements PermissionFactory {

    @Override
    public ImmutableList<Permission> getPermissions(Employee employee, ImmutableSet<EssRole> roles) {
        return roles.stream()
                .map(r -> permissionsForRole(employee, r))
                .flatMap(Collection::stream)
                .collect(Collectors.collectingAndThen(Collectors.toList(), ImmutableList::copyOf));
    }

    private Collection<Permission> permissionsForRole(Employee employee, EssRole role) {
        List<Permission> permissions = new ArrayList<>();
        if (role == EssRole.SENATE_EMPLOYEE) {
            permissions.addAll(new SenateStaffRole().permissions(employee));
        }
        if (role == EssRole.SUPPLY_EMPLOYEE) {
            permissions.addAll(new SupplyStaffRole().permissions(employee));
        }
        if (role == EssRole.SUPPLY_MANAGER) {
            permissions.addAll(new SupplyManagementRole().permissions(employee));
        }
        return permissions;
    }
}
