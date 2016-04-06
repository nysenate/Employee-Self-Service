package gov.nysenate.ess.supply.security;

import gov.nysenate.ess.core.model.auth.SenatePerson;
import gov.nysenate.ess.supply.security.role.SupplyRole;

import java.util.List;

public interface SupplyRoleDao {

    List<SupplyRole> getSupplyRoles(SenatePerson person);
}
