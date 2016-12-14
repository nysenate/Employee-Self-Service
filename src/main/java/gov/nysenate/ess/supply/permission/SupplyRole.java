package gov.nysenate.ess.supply.permission;

import com.google.common.collect.ImmutableList;
import gov.nysenate.ess.core.model.personnel.Employee;
import org.apache.shiro.authz.Permission;

public interface SupplyRole {

    ImmutableList<Permission> permissions(Employee employee);
}
