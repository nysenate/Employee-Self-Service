package gov.nysenate.ess.time.service.security.authorization;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import gov.nysenate.ess.core.model.auth.CorePermission;
import gov.nysenate.ess.core.model.auth.EssRole;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.service.security.authorization.permission.PermissionFactory;
import gov.nysenate.ess.time.model.auth.EssTimePermission;
import gov.nysenate.ess.time.model.auth.SimpleTimePermission;
import org.apache.shiro.authz.Permission;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static gov.nysenate.ess.core.model.auth.CorePermissionObject.EMPLOYEE_INFO;
import static gov.nysenate.ess.time.model.auth.TimePermissionObject.*;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * Grants permissions to members of personnel
 */
@Service
public class EssTimePersonnelPermissionFactory implements PermissionFactory {

    @Override
    public ImmutableList<Permission> getPermissions(Employee employee, ImmutableSet<Enum<?>> roles) {
        List<Permission> personnelPermissions = new ArrayList<>();

        if (roles.contains(EssRole.TIME_MANAGER) || roles.contains(EssRole.DEVELOPER)) {
            personnelPermissions.addAll(getPersonnelManagerPermissions());
        }

        return ImmutableList.copyOf(personnelPermissions);
    }

    /* --- Internal Methods --- */

    private List<Permission> getPersonnelManagerPermissions() {
        return ImmutableList.of(
                SimpleTimePermission.PERSONNEL_PAGES.getPermission(),

                new CorePermission(EMPLOYEE_INFO, GET),

                new EssTimePermission(TIME_RECORD_ACTIVE_YEARS, GET),
                new EssTimePermission(TIME_RECORDS, GET),
                new EssTimePermission(ATTENDANCE_RECORDS, GET),
                new EssTimePermission(ACCRUAL, GET),
                new EssTimePermission(ACCRUAL_ACTIVE_YEARS, GET),
                new EssTimePermission(ALLOWANCE, GET),
                new EssTimePermission(ALLOWANCE_ACTIVE_YEARS, GET)

        );
    }
}
