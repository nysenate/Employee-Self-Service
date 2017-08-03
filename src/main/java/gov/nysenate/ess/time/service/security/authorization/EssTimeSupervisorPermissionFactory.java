package gov.nysenate.ess.time.service.security.authorization;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Range;
import gov.nysenate.ess.core.model.auth.EssRole;
import gov.nysenate.ess.core.model.payroll.PayType;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.service.security.authorization.PermissionFactory;
import gov.nysenate.ess.core.util.DateUtils;
import gov.nysenate.ess.time.model.auth.EssTimePermission;
import gov.nysenate.ess.time.model.auth.SimpleTimePermission;
import gov.nysenate.ess.time.model.personnel.EmployeeSupInfo;
import gov.nysenate.ess.time.model.personnel.ExtendedSupEmpGroup;
import gov.nysenate.ess.time.model.personnel.PrimarySupEmpGroup;
import gov.nysenate.ess.time.service.attendance.TimeRecordService;
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
import java.util.stream.Stream;

import static gov.nysenate.ess.time.model.auth.TimePermissionObject.*;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * Grants the authenticated user permissions based on their role as an attendance supervisor
 */
@Component
public class EssTimeSupervisorPermissionFactory implements PermissionFactory {

    @Autowired private SupervisorInfoService supInfoService;
    @Autowired private TimeRecordService timeRecordService;

    @Override
    public ImmutableList<Permission> getPermissions(Employee employee, ImmutableSet<EssRole> roles) {
        int empId = employee.getEmployeeId();
        if (supInfoService.isSupervisor(empId) ||
                timeRecordService.hasActiveEmployeeRecord(empId)) {
            return ImmutableList.copyOf(getSupervisorPermissions(empId));
        }
        return ImmutableList.of();
    }

    /** --- Internal Methods --- */

    private List<Permission> getSupervisorPermissions(int supId) {
        ExtendedSupEmpGroup supEmpGroup = supInfoService.getExtendedSupEmpGroup(supId, Range.all());
        List<Permission> supPermissions = new ArrayList<>();

        // Add permission to use manage pages
        supPermissions.add(SimpleTimePermission.MANAGEMENT_PAGES.getPermission());

        // Add permission to view employee allowance if the supervisor has any temp employees
        supEmpGroup.getExtendedEmployeeSupInfos().parallelStream()
                .filter(empSupInfo -> empSupInfo.getPayType() == PayType.TE)
                .findAny().ifPresent((empSupInfo) ->
                        supPermissions.add(SimpleTimePermission.EMPLOYEE_ALLOWANCE_PAGE.getPermission()));

        // Add permissions to read data for both direct and indirect employees
        supEmpGroup.getExtendedEmployeeSupInfos().stream()
                .map(this::getEmployeeReadWritePermissions)
                .flatMap(Collection::stream)
                .forEach(supPermissions::add);

        // Add permissions to view supervisor information for primary employees
        supEmpGroup.getEmployeeSupEmpGroups().values().stream()
                .map(this::getSupervisorPermissions)
                .flatMap(Collection::stream)
                .forEach(supPermissions::add);

        return supPermissions;
    }

    private List<Permission> getEmployeeReadWritePermissions(EmployeeSupInfo supInfo) {
        List<Permission> readPermissions = getEmployeeReadPermissions(supInfo);
        List<Permission> writePermissions = getEmployeeWritePermissions(supInfo);
        return Stream.concat(readPermissions.stream(), writePermissions.stream())
                .collect(Collectors.toList());
    }

    /**
     * Returns permissions for reading an employee's attendance data
     * @param supInfo EmployeeSupInfo
     * @return List<Permission>
     */
    private List<Permission> getEmployeeReadPermissions(EmployeeSupInfo supInfo) {
        int empId = supInfo.getEmpId();
        Range<LocalDate> effectiveRange = supInfo.getEffectiveDateRange();
        return Arrays.asList(
                new EssTimePermission(empId, ATTENDANCE_RECORDS,        GET,    effectiveRange),
                new EssTimePermission(empId, ACCRUAL,                   GET,    effectiveRange),
                new EssTimePermission(empId, ALLOWANCE,                 GET,    getEffectiveYearRange(effectiveRange)),
                new EssTimePermission(empId, TIME_RECORD_ACTIVE_YEARS,  GET,    Range.all()),
                new EssTimePermission(empId, TIME_RECORDS,              GET,    effectiveRange)
        );
    }

    /**
     * Returns permissions for writing employee attendance data
     * @param supInfo EmployeeSupInfo
     * @return List<Permission>
     */
    private List<Permission> getEmployeeWritePermissions(EmployeeSupInfo supInfo) {
        int empId = supInfo.getEmpId();
        Range<LocalDate> effectiveRange = supInfo.getEffectiveDateRange();
        return Arrays.asList(
                new EssTimePermission(empId, TIME_RECORDS,              POST,   effectiveRange),
                new EssTimePermission(empId, TIME_RECORD_NOTIFICATION,  POST,   effectiveRange)
        );
    }

    /**
     * Return a list of permissions for supervisor functions for the given employees
     * @param supInfo {@link EmployeeSupInfo}
     * @return List<Permission>
     */
    private List<Permission> getSupervisorPermissions(PrimarySupEmpGroup supInfo) {
        int empId = supInfo.getSupervisorId();
        Range<LocalDate> effectiveRange = supInfo.getActiveDateRange();
        return Arrays.asList(
                new EssTimePermission(empId, SUPERVISOR_EMPLOYEES, GET, effectiveRange),
                new EssTimePermission(empId, SUPERVISOR_OVERRIDES, GET, effectiveRange),
                new EssTimePermission(empId, SUPERVISOR_TIME_RECORDS, GET, effectiveRange)
        );
    }

    /**
     * Get a range containing all days of all years present in the given range
     * @param effectiveRange Range<LocalDate>
     * @return Range<LocalDate>
     */
    private Range<LocalDate> getEffectiveYearRange(Range<LocalDate> effectiveRange) {
        int startYear = DateUtils.startOfDateRange(effectiveRange).getYear();
        int endYear = DateUtils.endOfDateRange(effectiveRange).getYear();

        return Range.closedOpen(
                LocalDate.ofYearDay(startYear, 1),
                LocalDate.ofYearDay(endYear + 1, 1)
        );
    }

}
