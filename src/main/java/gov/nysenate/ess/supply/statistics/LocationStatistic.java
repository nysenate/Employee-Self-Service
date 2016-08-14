package gov.nysenate.ess.supply.statistics;

import com.google.common.collect.ImmutableMap;
import gov.nysenate.ess.core.model.unit.Location;

import java.util.Map;

public class LocationStatistic {

    private final Location location;
//    private int totalOrders;
    private final ImmutableMap<String, Integer> itemOrderQuantities;

    public LocationStatistic(Location location, Map<String, Integer> itemOrderQuantities) {
        this.location = location;
        this.itemOrderQuantities = ImmutableMap.copyOf(itemOrderQuantities);
    }

    public Location getLocation() {
        return location;
    }

    public ImmutableMap<String, Integer> getItemOrderQuantities() {
        return itemOrderQuantities;
    }
}
