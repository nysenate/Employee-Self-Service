package gov.nysenate.ess.supply.security;

import com.google.common.collect.ImmutableCollection;
import gov.nysenate.ess.core.model.auth.SenatePerson;
import gov.nysenate.ess.supply.security.role.SupplyRole;

import java.util.List;

public interface SupplyRoleDao {

    ImmutableCollection<SupplyRole> getSupplyRoles(SenatePerson person);

    /**
     * We don't have a good way of getting all employees who work in supply.
     * This method gets around that by getting the uid's of all employees
     * with supply permissions. With the uid's we can figure out their
     * email address and get a Employee object using the EmployeeInfoService.
     */
    ImmutableCollection<String> getUidsWithSupplyPermissions();
}
