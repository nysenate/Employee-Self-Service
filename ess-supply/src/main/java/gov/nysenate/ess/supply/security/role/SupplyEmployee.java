package gov.nysenate.ess.supply.security.role;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SupplyEmployee implements SupplyRole {

    @Override
    public Collection<String> getPermissions() {
        List<String> permissions = new ArrayList<>();
        permissions.add("supply:order:view");
        permissions.add("supply:order:edit");
        permissions.add("supply:shipment:view");
        permissions.add("supply:shipment:edit");
        permissions.add("supply:shipment:process");
        permissions.add("supply:shipment:complete");
        permissions.add("supply:shipment:reject");
        return permissions;
    }
}
