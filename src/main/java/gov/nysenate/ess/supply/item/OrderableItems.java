package gov.nysenate.ess.supply.item;

import com.google.common.collect.ImmutableSet;
import gov.nysenate.ess.core.model.unit.LocationId;
import gov.nysenate.ess.supply.item.model.SupplyItem;

import java.util.Collection;
import java.util.stream.Collectors;

public class OrderableItems {

    /**
     * Returns a new set of SupplyItems with hidden and non expendable items removed.
     * @param items A collection of items to be filtered.
     * @return An ImmutableSet containing all visible and expendable items in the given <code>items</code> collection.
     * Returns an empty set if <code>items</code> is <code>null</code> or empty.
     */
    public static ImmutableSet<SupplyItem> forItems(Collection<SupplyItem> items) {
        if (items == null || items.isEmpty()) {
            return ImmutableSet.of();
        }
        return items.stream()
                .filter(SupplyItem::isExpendable)
                .filter(SupplyItem::isVisible)
                .collect(Collectors.collectingAndThen(Collectors.toList(), ImmutableSet::copyOf));
    }

    /**
     * @param items A collection of items to filter.
     * @param locId The location these items are being ordered for. If null, items are not filtered by location restrictions.
     * @return An ImmutableSet of supply items containing all visible and expendable items which
     * are allowed to be ordered from <code>locId</code> and were present in the <code>items</code> parameter.
     * Returns an empty set if <code>items</code> is <code>null</code> or empty.
     */
    public static ImmutableSet<SupplyItem> forItemsAndLoc(Collection<SupplyItem> items, LocationId locId) {
        return forItems(items).stream()
                .filter(i -> allowedToOrder(i, locId))
                .collect(Collectors.collectingAndThen(Collectors.toList(), ImmutableSet::copyOf));
    }

    private static boolean allowedToOrder(SupplyItem item, LocationId locId) {
        if (locId == null || !item.isRestricted()) {
            return true;
        }
        return item.isAllowed(locId);
    }
}
