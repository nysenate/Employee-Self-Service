package gov.nysenate.ess.travel.authorization.permission;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.service.security.authorization.permission.PermissionFactory;
import gov.nysenate.ess.travel.authorization.role.TravelRole;
import org.apache.shiro.authz.Permission;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.List;

@Service
public class TravelSosPermissionFactory implements PermissionFactory {

    @Override
    public ImmutableList<Permission> getPermissions(Employee employee, ImmutableSet<Enum> roles) {
        List<Permission> permissions = new ArrayList<>();
        if (roles.contains(TravelRole.SECRETARY_OF_THE_SENATE)) {
            permissions.add(SimpleTravelPermission.TRAVEL_UI_MANAGE.getPermission());
            permissions.add(SimpleTravelPermission.TRAVEL_UI_REVIEW.getPermission());
            permissions.add(SimpleTravelPermission.TRAVEL_UI_REVIEW_HISTORY.getPermission());
            permissions.add(SimpleTravelPermission.TRAVEL_UI_ASSIGN_DELEGATES.getPermission());
            permissions.add(new TravelPermissionBuilder()
                    .forAllEmps()
                    .forObject(TravelPermissionObject.TRAVEL_APPLICATION)
                    .forAction(RequestMethod.GET)
                    .buildPermission());

            permissions.add(new TravelPermissionBuilder()
                    .forAllEmps()
                    .forObject(TravelPermissionObject.TRAVEL_APPLICATION_REVIEW)
                    .forAction(RequestMethod.GET)
                    .forAction(RequestMethod.POST)
                    .buildPermission());
        }
        return ImmutableList.copyOf(permissions);
    }
}
