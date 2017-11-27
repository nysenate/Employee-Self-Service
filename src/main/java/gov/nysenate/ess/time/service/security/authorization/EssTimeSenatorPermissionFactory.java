package gov.nysenate.ess.time.service.security.authorization;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import gov.nysenate.ess.core.model.auth.EssRole;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.service.security.authorization.PermissionFactory;
import org.apache.shiro.authz.Permission;
import org.springframework.stereotype.Service;

import static gov.nysenate.ess.time.model.auth.SimpleTimePermission.ATTENDANCE_RECORD_PAGES;

/**
 * Grants time related permissions that are dependant on whether or not an employee is a senator
 */
@Service
public class EssTimeSenatorPermissionFactory implements PermissionFactory {

    /** {@inheritDoc} */
    @Override
    public ImmutableList<Permission> getPermissions(Employee employee, ImmutableSet<EssRole> roles) {
        if (roles.contains(EssRole.SENATOR)) {
            return getSenatorPermissions();
        }
        return getNonSenatorPermissions();
    }

    /* --- Internal Methods --- */

    /**
     * @return {@link ImmutableList<Permission>} permissions that are granted to senators.
     */
    public ImmutableList<Permission> getSenatorPermissions() {
        return ImmutableList.of();
    }

    /**
     * @return {@link ImmutableList<Permission>} permissions that are granted to users that aren't senators.
     */
    public ImmutableList<Permission> getNonSenatorPermissions() {
        return ImmutableList.<Permission>builder()
                .add(ATTENDANCE_RECORD_PAGES.getPermission())
                .build();
    }

}
