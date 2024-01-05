package gov.nysenate.ess.time.service.security.authorization;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.service.security.authorization.permission.PermissionFactory;
import gov.nysenate.ess.time.service.accrual.AccrualInfoService;
import org.apache.shiro.authz.Permission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.SortedSet;

import static gov.nysenate.ess.time.model.auth.SimpleTimePermission.ACCRUAL_PAGES;

/**
 * Grants permissions based on an employee's accrual status
 */
@Service
public class EssTimeAccrualPermissionFactory implements PermissionFactory {

    private final AccrualInfoService accInfoService;

    @Autowired
    public EssTimeAccrualPermissionFactory(AccrualInfoService accInfoService) {
        this.accInfoService = accInfoService;
    }

    @Override
    public ImmutableList<Permission> getPermissions(Employee employee, ImmutableSet<Enum<?>> roles) {
        int empId = employee.getEmployeeId();
        SortedSet<Integer> accrualYears = accInfoService.getAccrualYears(empId);
        // Permit access to accrual pages only if employee has ever been eligible for accruals
        if (accrualYears.isEmpty()) {
            return ImmutableList.of();
        } else {
            return ImmutableList.of(ACCRUAL_PAGES.getPermission());
        }
    }
}
