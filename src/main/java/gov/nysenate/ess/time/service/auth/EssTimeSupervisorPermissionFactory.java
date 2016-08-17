package gov.nysenate.ess.time.service.auth;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Range;
import com.google.common.collect.RangeSet;
import gov.nysenate.ess.core.model.auth.EssRole;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.service.auth.PermissionFactory;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import gov.nysenate.ess.time.model.auth.EssTimePermission;
import gov.nysenate.ess.time.model.auth.SimpleTimePermission;
import gov.nysenate.ess.time.model.personnel.EmployeeSupInfo;
import gov.nysenate.ess.time.model.personnel.SupervisorEmpGroup;
import gov.nysenate.ess.time.service.personnel.SupervisorInfoService;
import org.apache.shiro.authz.Permission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static gov.nysenate.ess.time.model.auth.TimePermissionObject.*;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * Grants the authenticated user permissions based on their role as an attendance supervisor
 */
@Component
public class EssTimeSupervisorPermissionFactory implements PermissionFactory {

    @Autowired SupervisorInfoService supInfoService;
    @Autowired EmployeeInfoService empInfoService;

    @Override
    public ImmutableList<Permission> getPermissions(Employee employee, ImmutableSet<EssRole> roles) {
        int empId = employee.getEmployeeId();
        RangeSet<LocalDate> activeServiceDates = empInfoService.getEmployeeActiveDatesService(empId);
        if (!supInfoService.isSupervisorDuring(empId, activeServiceDates.span())) {
            return ImmutableList.of();
        }
        return ImmutableList.copyOf(getPermissions(empId));
    }

    /** --- Internal Methods --- */

    private List<Permission> getPermissions(int supId) {
        SupervisorEmpGroup supEmpGroup = supInfoService.getSupervisorEmpGroup(supId, Range.all());
        List<Permission> supPermissions = new ArrayList<>();

        // Add permission to use manage pages
        supPermissions.add(SimpleTimePermission.MANAGEMENT_PAGES.getPermission());

        // Add permissions to view/post employee data
        supEmpGroup.getAllEmployees().stream()
                .map(this::getEmployeePermissions)
                .flatMap(Collection::stream)
                .forEach(supPermissions::add);

        return supPermissions;
    }

    /**
     * Determines the permissions that the supervisor has over an employee's data
     * @param supInfo EmployeeSupInfo
     * @return List<Permission>
     */
    private List<Permission> getEmployeePermissions(EmployeeSupInfo supInfo) {
        int empId = supInfo.getEmpId();
        Range<LocalDate> effectiveRange = supInfo.getEffectiveDateRange();
        return Arrays.asList(
                new EssTimePermission(empId, ATTENDANCE_RECORDS,        GET,    effectiveRange),
                new EssTimePermission(empId, TIME_RECORD_ACTIVE_YEARS,  GET,    Range.all()),
                new EssTimePermission(empId, TIME_RECORDS,              GET,    effectiveRange),
                new EssTimePermission(empId, TIME_RECORDS,              POST,   effectiveRange),
                new EssTimePermission(empId, TIME_RECORD_NOTIFICATION,  POST,   effectiveRange)
        );
    }

}
