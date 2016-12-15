package gov.nysenate.ess.supply.item;

import com.google.common.collect.ImmutableSet;
import gov.nysenate.ess.core.model.unit.LocationId;
import gov.nysenate.ess.supply.item.model.SupplyItem;

import java.util.Collection;
import java.util.stream.Collectors;

public class OrderableItems {

    public static ImmutableSet<SupplyItem> forItems(Collection<SupplyItem> items) {
        if (items == null || items.isEmpty()) {
            return ImmutableSet.of();
        }
        return items.stream()
                .filter(SupplyItem::isExpendable)
                .filter(SupplyItem::isVisible)
                .collect(Collectors.collectingAndThen(Collectors.toList(), ImmutableSet::copyOf));
    }

    public static ImmutableSet<SupplyItem> forItemsAndLoc(Collection<SupplyItem> items, LocationId locId) {
        return forItems(items).stream()
                .filter(i -> allowedToOrder(i, locId))
                .collect(Collectors.collectingAndThen(Collectors.toList(), ImmutableSet::copyOf));
    }

    private static boolean allowedToOrder(SupplyItem item, LocationId locId) {
        if (!item.isRestricted()) {
            return true;
        }
        return item.isAllowed(locId);
    }
}
