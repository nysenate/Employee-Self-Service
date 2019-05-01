package gov.nysenate.ess.travel.authorization.permission;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import gov.nysenate.ess.core.model.auth.CorePermission;
import gov.nysenate.ess.core.model.auth.CorePermissionObject;
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
            permissions.add(TravelPermission.TRAVEL_UI_MANAGE.getPermission());
            permissions.add(TravelPermission.TRAVEL_UI_REVIEW.getPermission());
            permissions.add(TravelPermission.TRAVEL_UI_REVIEW_HISTORY.getPermission());
            permissions.add(new CorePermission(CorePermissionObject.TRAVEL_APPLICATION, RequestMethod.GET));
            permissions.add(new CorePermission(CorePermissionObject.TRAVEL_APPLICATION, RequestMethod.POST));
            permissions.add(new CorePermission(CorePermissionObject.TRAVEL_APPLICATION_APPROVAL, RequestMethod.GET));
            permissions.add(new CorePermission(CorePermissionObject.TRAVEL_APPLICATION_APPROVAL, RequestMethod.POST));
        }
        return ImmutableList.copyOf(permissions);
    }
}
