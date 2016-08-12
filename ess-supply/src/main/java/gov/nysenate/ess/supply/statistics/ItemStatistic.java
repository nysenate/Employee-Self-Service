package gov.nysenate.ess.supply.statistics;

import com.google.common.collect.ImmutableMap;
import gov.nysenate.ess.supply.item.SupplyItem;

import java.util.Map;

public class ItemStatistic {

    private final SupplyItem item;
    //private final int totalOrdered;
    private final ImmutableMap<String, Integer> locationOrderQauntities;

    public ItemStatistic(SupplyItem item, Map<String, Integer> locationOrderQauntities) {
        this.item = item;
        this.locationOrderQauntities = ImmutableMap.copyOf(locationOrderQauntities);
    }
}
