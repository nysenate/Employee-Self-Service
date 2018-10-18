package gov.nysenate.ess.supply.authorization;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import gov.nysenate.ess.core.model.auth.EssRole;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.service.security.authorization.PermissionFactory;
import gov.nysenate.ess.supply.authorization.permission.RequisitionPermission;
import gov.nysenate.ess.supply.authorization.permission.SupplyPermission;
import org.apache.shiro.authz.Permission;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;

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
            permissions.add(RequisitionPermission.forCustomer(employee.getEmployeeId(), RequestMethod.GET));
            permissions.add(RequisitionPermission.forDestination(employee.getWorkLocation().getLocId().toString(), RequestMethod.GET));
        }
        if (role == EssRole.SUPPLY_EMPLOYEE) {
            permissions.add(SupplyPermission.SUPPLY_EMPLOYEE.getPermission());
            permissions.add(RequisitionPermission.forAll(RequestMethod.GET));
            permissions.add(RequisitionPermission.forAll(RequestMethod.POST));
        }
        if (role == EssRole.SUPPLY_MANAGER) {
            permissions.add(SupplyPermission.SUPPLY_REQUISITION_APPROVE.getPermission());
        }
        return permissions;
    }
}
