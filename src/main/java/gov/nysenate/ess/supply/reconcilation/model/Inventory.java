package gov.nysenate.ess.supply.reconcilation.model;

import com.google.common.collect.ImmutableMap;
import gov.nysenate.ess.core.model.unit.LocationId;

import java.util.Map;

public class Inventory {

    private final LocationId locationId;
    private final ImmutableMap<Integer, Integer> itemQuantities;

    public Inventory(LocationId locationId, Map<Integer, Integer> itemQuantities) {
        this.locationId = locationId;
        this.itemQuantities = ImmutableMap.copyOf(itemQuantities);
    }

    public boolean containsItem(int itemId) {
        return getItemQuantities().get(itemId) != null;
    }

    public LocationId getLocationId() {
        return locationId;
    }

    public Map<Integer, Integer> getItemQuantities() {
        return itemQuantities;
    }
}
