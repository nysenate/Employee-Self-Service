package gov.nysenate.ess.supply.security.role;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SupplyManager implements SupplyRole {

    @Override
    public Collection<String> getPermissions() {
        List<String> permissions = new ArrayList<>();
        permissions.add("supply:shipment:approve");
        return permissions;
    }
}
