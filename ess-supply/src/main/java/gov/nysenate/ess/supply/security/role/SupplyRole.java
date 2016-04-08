package gov.nysenate.ess.supply.security.role;

import com.google.common.collect.ImmutableCollection;

public interface SupplyRole {

    ImmutableCollection<String> getPermissions();
}
