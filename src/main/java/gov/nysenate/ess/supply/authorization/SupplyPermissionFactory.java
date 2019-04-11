package gov.nysenate.ess.supply.authorization;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import gov.nysenate.ess.core.model.auth.EssRole;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.service.security.authorization.permission.PermissionFactory;
import gov.nysenate.ess.supply.authorization.permission.RequisitionPermission;
import gov.nysenate.ess.supply.authorization.permission.SupplyPermission;
import org.apache.shiro.authz.Permission;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static gov.nysenate.ess.supply.authorization.permission.SupplyPermission.SUPPLY_EMPLOYEE;

@Component
public class SupplyPermissionFactory implements PermissionFactory {

    @Override
    public ImmutableList<Permission> getPermissions(Employee employee, ImmutableSet<Enum> roles) {
        return roles.stream()
                .map(r -> permissionsForRole(employee, r))
                .flatMap(Collection::stream)
                .collect(Collectors.collectingAndThen(Collectors.toList(), ImmutableList::copyOf));
    }

    private Collection<Permission> permissionsForRole(Employee employee, Enum role) {
        List<Permission> permissions = new ArrayList<>();
        if (role.equals(EssRole.SENATE_EMPLOYEE)) {
            permissions.add(RequisitionPermission.forCustomer(employee.getEmployeeId(), RequestMethod.GET));
            permissions.add(RequisitionPermission.forDestination(employee.getWorkLocation().getLocId().toString(), RequestMethod.GET));
        }
        else if (role.equals(EssRole.SUPPLY_EMPLOYEE)) {
            permissions.add(SupplyPermission.SUPPLY_UI_NAV_MANAGE.getPermission());
            permissions.add(SupplyPermission.SUPPLY_UI_MANAGE.getPermission());
            permissions.add(SUPPLY_EMPLOYEE.getPermission());
            permissions.add(SupplyPermission.SUPPLY_STAFF_VIEW.getPermission());
            permissions.add(RequisitionPermission.forAll(RequestMethod.GET));
            permissions.add(RequisitionPermission.forAll(RequestMethod.POST));
        }
        else if (role.equals(EssRole.SUPPLY_MANAGER)) {
            permissions.add(SupplyPermission.SUPPLY_REQUISITION_APPROVE.getPermission());
        }
        else if (role.equals(EssRole.SUPPLY_REPORTER)) {
            permissions.add(RequisitionPermission.forAll(RequestMethod.GET));
            permissions.add(SupplyPermission.SUPPLY_STAFF_VIEW.getPermission());
            permissions.add(SupplyPermission.SUPPLY_UI_NAV_MANAGE.getPermission());
            permissions.add(SupplyPermission.SUPPLY_UI_MANAGE_REQUISITION_HISTORY.getPermission());
            permissions.add(SupplyPermission.SUPPLY_UI_MANAGE_ITEM_HISTORY.getPermission());
        }

        return permissions;
    }
}
