package gov.nysenate.ess.time.service.security.authorization;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import gov.nysenate.ess.core.model.auth.EssRole;
import gov.nysenate.ess.core.model.payroll.PayType;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.service.security.authorization.PermissionFactory;
import org.apache.shiro.authz.Permission;
import org.springframework.stereotype.Service;

import static gov.nysenate.ess.time.model.auth.SimpleTimePermission.ACCRUAL_PROJECTIONS;
import static gov.nysenate.ess.time.model.auth.SimpleTimePermission.ALLOWANCE_PAGE;

/**
 * Grant permissions to employees based on their current {@link PayType pay type}
 */
@Service
public class EssTimePayTypePermissionFactory implements PermissionFactory {

    /** {@inheritDoc} */
    @Override
    public ImmutableList<Permission> getPermissions(Employee employee, ImmutableSet<EssRole> roles) {
        PayType payType = employee.getPayType();
        ImmutableList.Builder<Permission> permListBldr = ImmutableList.builder();
        if (payType != null) {
            switch (payType) {
                case SA:
                case RA:
                    permListBldr.add(ACCRUAL_PROJECTIONS.getPermission());
                    break;
                case TE:
                    permListBldr.add(ALLOWANCE_PAGE.getPermission());
                    break;
            }
        }
        return permListBldr.build();
    }
}
