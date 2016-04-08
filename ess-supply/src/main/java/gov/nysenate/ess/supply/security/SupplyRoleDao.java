package gov.nysenate.ess.supply.security;

import com.google.common.collect.ImmutableCollection;
import gov.nysenate.ess.core.model.auth.SenatePerson;
import gov.nysenate.ess.supply.security.role.SupplyRole;

import java.util.List;

public interface SupplyRoleDao {

    ImmutableCollection<SupplyRole> getSupplyRoles(SenatePerson person);
}
