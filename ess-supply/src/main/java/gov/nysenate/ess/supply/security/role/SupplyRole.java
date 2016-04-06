package gov.nysenate.ess.supply.security.role;

import java.util.Collection;

public interface SupplyRole {

    Collection<String> getPermissions();
}
