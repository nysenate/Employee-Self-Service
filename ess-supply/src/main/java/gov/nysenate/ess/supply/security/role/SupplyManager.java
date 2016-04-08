package gov.nysenate.ess.supply.security.role;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SupplyManager implements SupplyRole {

    @Override
    public ImmutableCollection<String> getPermissions() {
        List<String> permissions = new ArrayList<>();
        permissions.add("supply:shipment:approve");
        return ImmutableList.copyOf(permissions);
    }
}
