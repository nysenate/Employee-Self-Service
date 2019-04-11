package gov.nysenate.ess.travel.authorization;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Range;
import gov.nysenate.ess.core.model.auth.CorePermission;
import gov.nysenate.ess.core.model.auth.CorePermissionObject;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.service.security.authorization.permission.PermissionFactory;
import gov.nysenate.ess.time.model.personnel.SupervisorEmpGroup;
import gov.nysenate.ess.time.service.personnel.SupervisorInfoService;
import gov.nysenate.ess.travel.authorization.permission.TravelPermission;
import org.apache.shiro.authz.Permission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.List;

/**
 * Department Head permission factory for Travel.
 * // FIXME Currently uses supervisors instead of dept heads since dept head data is not in the database.
 */
public class TravelDeptHdPermissionFactory implements PermissionFactory {

    @Autowired private SupervisorInfoService supInfoService;

    @Override
    public ImmutableList<Permission> getPermissions(Employee employee, ImmutableSet<Enum> roles) {
        List<Permission> permissions = new ArrayList<>();
        if (supInfoService.isSupervisor(employee.getEmployeeId())) {
            permissions.add(TravelPermission.TRAVEL_UI_APPROVAL.getPermission());
            permissions.addAll(empGroupPermissions(employee));
        }
        return ImmutableList.copyOf(permissions);
    }

    private ImmutableList<Permission> empGroupPermissions(Employee employee) {
        SupervisorEmpGroup empGroup = supInfoService.getSupervisorEmpGroup(employee.getEmployeeId(), Range.all()); // TODO Range.all() correct logic?
        return empGroup.getPrimaryEmployees().values().stream()
                .flatMap(e -> ImmutableList.of(
                        new CorePermission(e.getEmpId(), CorePermissionObject.TRAVEL_APPLICATION, RequestMethod.GET),
                        new CorePermission(e.getEmpId(), CorePermissionObject.TRAVEL_APPLICATION, RequestMethod.POST),
                        new CorePermission(e.getEmpId(), CorePermissionObject.TRAVEL_APPLICATION_APPROVAL, RequestMethod.GET),
                        new CorePermission(e.getEmpId(), CorePermissionObject.TRAVEL_APPLICATION_APPROVAL, RequestMethod.POST)
                ).stream())
                .collect(ImmutableList.toImmutableList());
    }
}
