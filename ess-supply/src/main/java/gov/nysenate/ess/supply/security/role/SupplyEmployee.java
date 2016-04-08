package gov.nysenate.ess.supply.security.role;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SupplyEmployee implements SupplyRole {

    @Override
    public ImmutableCollection<String> getPermissions() {
        List<String> permissions = new ArrayList<>();
        permissions.add("supply:order:view");
        permissions.add("supply:order:edit");
        permissions.add("supply:shipment:manage"); // Can view supply manage page's
        permissions.add("supply:shipment:view");
        permissions.add("supply:shipment:edit");
        permissions.add("supply:shipment:process");
        permissions.add("supply:shipment:complete");
        permissions.add("supply:shipment:reject");
        return ImmutableList.copyOf(permissions);
    }
}
