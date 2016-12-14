package gov.nysenate.ess.supply.item;

import com.google.common.collect.ImmutableSet;
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
}
