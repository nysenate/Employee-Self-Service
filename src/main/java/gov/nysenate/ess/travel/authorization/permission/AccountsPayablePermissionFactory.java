package gov.nysenate.ess.travel.authorization.permission;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import gov.nysenate.ess.core.model.auth.EssRole;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.service.security.authorization.permission.PermissionFactory;
import org.apache.shiro.authz.Permission;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.List;

@Service
public class AccountsPayablePermissionFactory implements PermissionFactory {

    @Override
    public ImmutableList<Permission> getPermissions(Employee employee, ImmutableSet<Enum> roles) {
        List<Permission> permissions = new ArrayList<>();
        if (roles.contains(EssRole.ACCOUNTS_PAYABLE)) {
            permissions.add(SimpleTravelPermission.TRAVEL_UI_RECONCILE_TRAVEL.getPermission());
            permissions.add(new TravelPermissionBuilder()
                    .forAllEmps()
                    .forObject(TravelPermissionObject.TRAVEL_APPLICATION)
                    .forObject(TravelPermissionObject.TRAVEL_APPLICATION_REVIEW)
                    .forAction(RequestMethod.GET)
                    .buildPermission());
        }
        return ImmutableList.copyOf(permissions);
    }
}
