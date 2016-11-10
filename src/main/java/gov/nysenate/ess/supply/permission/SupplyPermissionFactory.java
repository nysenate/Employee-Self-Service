package gov.nysenate.ess.supply.permission;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import gov.nysenate.ess.core.model.auth.EssRole;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.model.unit.Location;
import gov.nysenate.ess.core.service.auth.PermissionFactory;
import gov.nysenate.ess.core.service.unit.LocationService;
import org.apache.shiro.authz.Permission;
import org.apache.shiro.authz.permission.WildcardPermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Component
public class SupplyPermissionFactory implements PermissionFactory {

    @Autowired private LocationService locationService;

    @Override
    public ImmutableList<Permission> getPermissions(Employee employee, ImmutableSet<EssRole> roles) {
        Location location = locationService.getLocation(employee.getWorkLocation().getLocId());
        List<Permission> permissions = new ArrayList<>();
        for (EssRole role : roles) {
            permissions.addAll(permissionsForRole(employee, location, role));
        }
        return ImmutableList.copyOf(permissions);
    }

    private Collection<Permission> permissionsForRole(Employee employee, Location location, EssRole role) {
        List<Permission> permissions = new ArrayList<>();
        if (role == EssRole.SENATE_EMPLOYEE) {
            permissions.addAll(senateEmployeePermissions(employee, location));
        }
        if (role == EssRole.SUPPLY_EMPLOYEE) {
            permissions.addAll(supplyEmployeePermissions());
        }
        if (role == EssRole.SUPPLY_MANAGER) {
            permissions.addAll(supplyManagerPermissions());
        }
        return permissions;
    }

    /**
     * Senate employees have permissions to view their own orders and orders from their work location.
     */
    private Collection<Permission> senateEmployeePermissions(Employee employee, Location location) {
        List<Permission> permissions = new ArrayList<>();
        permissions.add(new WildcardPermission("supply:order:view:customer:" + String.valueOf(employee.getEmployeeId())));
        permissions.add(new WildcardPermission("supply:order:view:destination:" + location.getLocId().toString()));
        return permissions;
    }

    private Collection<Permission> supplyEmployeePermissions() {
        List<Permission> permissions = new ArrayList<>();
        permissions.add(new WildcardPermission("supply:employee")); // Generic supply employee permissions.
        permissions.add(new WildcardPermission("supply:order:view")); // Can view all orders.
        permissions.add(new WildcardPermission("supply:order:edit")); // Can edit all orders.
        permissions.add(new WildcardPermission("supply:shipment:manage")); // Can view manage page's
        return permissions;
    }

    private Collection<Permission> supplyManagerPermissions() {
        List<Permission> permissions = new ArrayList<>();
        permissions.add(new WildcardPermission("supply:shipment:approve"));
        return permissions;
    }
}
