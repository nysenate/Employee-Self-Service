package gov.nysenate.ess.core.service.security.authorization;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import gov.nysenate.ess.core.model.auth.EssRole;
import gov.nysenate.ess.core.model.personnel.Employee;
import org.apache.shiro.authz.Permission;
import org.springframework.stereotype.Service;

import static gov.nysenate.ess.core.model.auth.EssRole.ACK_MANAGER;
import static gov.nysenate.ess.core.model.auth.SimpleEssPermission.ACK_REPORT_GENERATION;

/**
 * Grants permissions for acknowledgment related functionality.
 */
@Service
public class AcknowledgmentPermissionFactory implements PermissionFactory {

    @Override
    public ImmutableList<Permission> getPermissions(Employee employee, ImmutableSet<EssRole> roles) {
        if (roles.contains(ACK_MANAGER)) {
            return ImmutableList.of(ACK_REPORT_GENERATION.getPermission());
        }
        return ImmutableList.of();
    }
}
