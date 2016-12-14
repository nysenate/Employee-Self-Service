package gov.nysenate.ess.supply.permission;

import com.google.common.collect.ImmutableList;
import gov.nysenate.ess.core.model.personnel.Employee;
import org.apache.shiro.authz.Permission;
import org.apache.shiro.authz.permission.WildcardPermission;

import java.util.ArrayList;
import java.util.List;

public class SupplyStaffRole implements SupplyRole {

    @Override
    public ImmutableList<Permission> permissions(Employee employee) {
        List<Permission> permissions = new ArrayList<>();
        permissions.add(new WildcardPermission("supply:employee")); // Generic supply employee permissions.
        permissions.add(new WildcardPermission("supply:requisition:view")); // Can view all orders.
        permissions.add(new WildcardPermission("supply:requisition:edit")); // Can edit all orders.
        return ImmutableList.copyOf(permissions);
    }
}
