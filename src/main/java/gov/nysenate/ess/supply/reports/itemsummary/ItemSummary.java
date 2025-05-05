package gov.nysenate.ess.supply.reports.itemsummary;

import gov.nysenate.ess.supply.item.model.SupplyItem;
import gov.nysenate.ess.supply.requisition.model.Requisition;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ItemSummary {
    private final SupplyItem item;
    private final int totalQuantity;
    private final List<ItemOccurrence> occurrences;

    private ItemSummary(SupplyItem item, int totalQuantity, List<ItemOccurrence> occurrences) {
        this.item = item;
        this.totalQuantity = totalQuantity;
        this.occurrences = occurrences;
    }

    /**
     * Creates an {@link ItemSummary} for a given {@link SupplyItem} by aggregating its occurrences
     * across a list of {@link Requisition}s. The resulting summary includes all matching item occurrences,
     * sorted first by destination location code and then by the requisition's ordered date and time.
     *
     * @param item         the {@link SupplyItem} to summarize
     * @param requisitions the list of {@link Requisition}s to search for matching line items
     * @return an {@link ItemSummary} containing the item, total quantity, and sorted list of occurrences
     */
    public static ItemSummary of(SupplyItem item, List<Requisition> requisitions) {
        List<ItemOccurrence> occurrences = requisitions.stream()
                .flatMap(req -> req.getLineItems().stream()
                        .filter(li -> li.getItem().getId() == item.getId())
                        .map(li -> new ItemOccurrence(item.getId(), li.getQuantity(), req)))
                .sorted(Comparator
                        .comparing((ItemOccurrence io) -> io.getRequisition().getDestination().getLocId().getCode())
                        .thenComparing(io -> io.getRequisition().getOrderedDateTime()))
                .collect(Collectors.toList());
        int totalQuantity = occurrences.stream().mapToInt(ItemOccurrence::getQuantity).sum();
        return new ItemSummary(item, totalQuantity, occurrences);
    }

    public SupplyItem getItem() {
        return item;
    }

    public int getTotalQuantity() {
        return totalQuantity;
    }

    public List<ItemOccurrence> getOccurrences() {
        return occurrences;
    }
}
