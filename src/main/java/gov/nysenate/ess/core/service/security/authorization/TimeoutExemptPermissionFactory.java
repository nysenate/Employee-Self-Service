package gov.nysenate.ess.core.service.security.authorization;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import gov.nysenate.ess.core.model.auth.EssRole;
import gov.nysenate.ess.core.model.auth.SimpleEssPermission;
import gov.nysenate.ess.core.model.personnel.Employee;
import org.apache.shiro.authz.Permission;
import org.springframework.stereotype.Component;

import static gov.nysenate.ess.core.model.auth.EssRole.TIMEOUT_EXEMPT;

/**
 * Created by Chenguang He on 7/25/2016.
 */
@Component
public class TimeoutExemptPermissionFactory implements PermissionFactory {

    @Override
    public ImmutableList<Permission> getPermissions(Employee employee, ImmutableSet<EssRole> roles) {
        return roles.contains(TIMEOUT_EXEMPT)
                ? ImmutableList.of(SimpleEssPermission.TIMEOUT_EXEMPT.getPermission())
                : ImmutableList.of();
    }
}