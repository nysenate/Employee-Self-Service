package gov.nysenate.ess.time.service.auth;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import gov.nysenate.ess.core.model.auth.EssRole;
import gov.nysenate.ess.core.model.payroll.PayType;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.service.auth.PermissionFactory;
import gov.nysenate.ess.time.model.auth.SimpleTimePermission;
import org.apache.shiro.authz.Permission;
import org.springframework.stereotype.Service;

/**
 * Grant Regular and Special Annual employees permission to use the accrual projections page
 */
@Service
public class EssTimePayTypePermissionFactory implements PermissionFactory {

    /** {@inheritDoc} */
    @Override
    public ImmutableList<Permission> getPermissions(Employee employee, ImmutableSet<EssRole> roles) {
        PayType payType = employee.getPayType();
        switch (payType) {
            case SA:
            case RA:
                return ImmutableList.of(SimpleTimePermission.ACCRUAL_PROJECTIONS.getPermission());
            default:
                return ImmutableList.of();
        }
    }
}
