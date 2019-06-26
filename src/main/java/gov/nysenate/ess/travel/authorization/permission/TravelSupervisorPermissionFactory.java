package gov.nysenate.ess.travel.authorization.permission;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Range;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.service.security.authorization.permission.PermissionFactory;
import gov.nysenate.ess.time.model.personnel.EmployeeSupInfo;
import gov.nysenate.ess.time.model.personnel.SupervisorEmpGroup;
import gov.nysenate.ess.time.model.personnel.SupervisorMissingEmpsEx;
import gov.nysenate.ess.time.service.personnel.SupervisorInfoService;
import gov.nysenate.ess.travel.authorization.role.TravelRole;
import org.apache.shiro.authz.Permission;
import org.apache.shiro.authz.permission.WildcardPermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Supervisor permission factory for Travel.
 */
@Service
public class TravelSupervisorPermissionFactory implements PermissionFactory {

    @Autowired private SupervisorInfoService supInfoService;

    @Override
    public ImmutableList<Permission> getPermissions(Employee employee, ImmutableSet<Enum> roles) {
        List<Permission> permissions = new ArrayList<>();
        if (roles.contains(TravelRole.SUPERVISOR)) {
            permissions.add(SimpleTravelPermission.TRAVEL_UI_MANAGE.getPermission());
            permissions.add(SimpleTravelPermission.TRAVEL_UI_REVIEW.getPermission());
            permissions.add(SimpleTravelPermission.TRAVEL_UI_REVIEW_HISTORY.getPermission());
            if (!roles.contains(TravelRole.DELEGATE)) {
                // Delegates do not get permission to modify delegates.
                permissions.add(SimpleTravelPermission.TRAVEL_ASSIGN_DELEGATES.getPermission());
            }
            permissions.addAll(empGroupPermissions(employee));
        }
        return ImmutableList.copyOf(permissions);
    }

    private ImmutableList<Permission> empGroupPermissions(Employee employee) {
        SupervisorEmpGroup empGroup;
        try {
            empGroup = supInfoService.getSupervisorEmpGroup(employee.getEmployeeId(), Range.all()); // TODO Range.all() correct logic?
        } catch(SupervisorMissingEmpsEx ex) {
            // If this employee is not a supervisor, i.e. supervisor delegate, skip these permissions.
            return ImmutableList.of();
        }
        Collection<Integer> supEmpIds = empGroup.getPrimaryEmployees().values().stream()
                .mapToInt(EmployeeSupInfo::getEmpId)
                .boxed()
                .collect(Collectors.toList());

        WildcardPermission appPermission = new TravelPermissionBuilder()
                .forEmpIds(supEmpIds)
                .forObject(TravelPermissionObject.TRAVEL_APPLICATION)
                .forAction(RequestMethod.GET)
                .buildPermission();

        WildcardPermission appReviewPermission = new TravelPermissionBuilder()
                .forEmpIds(supEmpIds)
                .forObject(TravelPermissionObject.TRAVEL_APPLICATION_REVIEW)
                .forAction(RequestMethod.GET)
                .forAction(RequestMethod.POST)
                .buildPermission();

        return ImmutableList.of(appPermission, appReviewPermission);
    }
}
