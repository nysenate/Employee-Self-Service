package gov.nysenate.ess.supply.permission;

import com.google.common.collect.ImmutableList;
import gov.nysenate.ess.core.model.personnel.Employee;
import org.apache.shiro.authz.Permission;
import org.apache.shiro.authz.permission.WildcardPermission;

import java.util.ArrayList;
import java.util.List;

public class SupplyManagementRole implements SupplyRole {

    @Override
    public ImmutableList<Permission> permissions(Employee employee) {
        List<Permission> permissions = new ArrayList<>();
        permissions.add(new WildcardPermission("supply:requisition:approve"));
        return ImmutableList.copyOf(permissions);
    }
}
