package gov.nysenate.ess.core.service.security.authorization.permission;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import gov.nysenate.ess.core.model.auth.CorePermission;
import gov.nysenate.ess.core.model.personnel.Employee;
import org.apache.shiro.authz.Permission;
import org.springframework.stereotype.Service;

import static gov.nysenate.ess.core.model.auth.CorePermissionObject.EMPLOYEE_INFO;
import static gov.nysenate.ess.core.model.auth.CorePermissionObject.PERSONNEL_TASK;
import static gov.nysenate.ess.core.model.auth.EssRole.PERSONNEL_COMPLIANCE_MANAGER;
import static gov.nysenate.ess.core.model.auth.SimpleEssPermission.COMPLIANCE_REPORT_GENERATION;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * Grants permissions for personnel compliance task related functionality.
 */
@Service
public class PersonnelCompliancePermissionFactory implements PermissionFactory {

    @Override
    public ImmutableList<Permission> getPermissions(Employee employee, ImmutableSet<Enum<?>> roles) {
        if (roles.contains(PERSONNEL_COMPLIANCE_MANAGER)) {
            return ImmutableList.of(
                    // Permission to view reports of task compliance.
                    COMPLIANCE_REPORT_GENERATION.getPermission(),
                    // Permission to see basic info for all employees
                    new CorePermission(EMPLOYEE_INFO, GET),
                    // Permission to modify personnel assigned tasks for all employees.
                    new CorePermission(PERSONNEL_TASK, POST)
            );
        }
        return ImmutableList.of();
    }
}
