package gov.nysenate.ess.travel.authorization.permission;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.service.security.authorization.permission.PermissionFactory;
import org.apache.shiro.authz.Permission;
import org.apache.shiro.authz.permission.WildcardPermission;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.List;

/**
 * Grants travel permissions to regular senate employees.
 */
@Component
public class TravelEmployeePermissionFactory implements PermissionFactory {

    @Override
    public ImmutableList<Permission> getPermissions(Employee employee, ImmutableSet<Enum<?>> roles) {
        List<Permission> permissions = new ArrayList<>();
        WildcardPermission appPermission = new TravelPermissionBuilder()
                .forEmpId(employee.getEmployeeId())
                .forObject(TravelPermissionObject.TRAVEL_APPLICATION)
                .forAction(RequestMethod.GET)
                .forAction(RequestMethod.POST)
                .buildPermission();
        permissions.add(appPermission);
        if (!employee.isSenator()) {
            permissions.add(SimpleTravelPermission.TRAVEL_SUBMIT_APP.getPermission());
        }

        return ImmutableList.copyOf(permissions);
    }
}
