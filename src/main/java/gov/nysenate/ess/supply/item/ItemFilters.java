package gov.nysenate.ess.supply.item;

import gov.nysenate.ess.core.util.LimitOffset;
import gov.nysenate.ess.core.util.PaginatedList;
import gov.nysenate.ess.supply.item.model.SupplyItem;

import java.util.*;
import java.util.stream.Collectors;

public class ItemFilters {

    public static Set<SupplyItem> byCategories(Set<SupplyItem> items, Set<String> categoryNames) {
        if (categoryNames.isEmpty()) {
            return items;
        }
        return items.stream()
                .filter(i -> categoryNames.contains(i.getCategory().getName()))
                .collect(Collectors.toSet());
    }

    public static Set<SupplyItem> byTerm(Set<SupplyItem> items, String term) {
        if (term.isEmpty()) {
            return items;
        }
        return items.stream()
                .filter(i -> i.getDescription().toUpperCase().contains(term.toUpperCase()))
                .collect(Collectors.toSet());
    }

    public static List<SupplyItem> bySort(Collection<SupplyItem> items, String sort) {
        List<SupplyItem> sortedList = new ArrayList<>(items);
        if (sort.equalsIgnoreCase("Category")) {
            Collections.sort(sortedList, Comparator.comparing(SupplyItem::getCategory));
        } else {
            // Sort by Description (labeled name in UI) by default
            Collections.sort(sortedList, Comparator.comparing(SupplyItem::getDescription));
        }
        return sortedList;
    }

    public static PaginatedList<SupplyItem> byLimitOffset(List<SupplyItem> items, LimitOffset limOff) {
        List<SupplyItem> pageItems = LimitOffset.limitList(items, limOff);
        return new PaginatedList<>(items.size(), limOff, pageItems);
    }
}
