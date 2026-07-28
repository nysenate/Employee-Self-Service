package gov.nysenate.ess.supply.reports.itemsummary;

import gov.nysenate.ess.supply.item.LineItem;
import gov.nysenate.ess.supply.item.model.SupplyItem;
import gov.nysenate.ess.supply.requisition.model.Requisition;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;


public class RequisitionFilter {
    private final List<Predicate<Requisition>> conditions = new ArrayList<>();

    public static boolean matchesCommodityCode(SupplyItem item, @Nullable String filter) {
        if (filter == null || filter.isBlank()) {
            return true;
        }
        return item.getCommodityCode().toLowerCase().contains(filter.toLowerCase());
    }

    /**
     * Adds a filter condition that matches requisitions containing at least one line item
     * whose item's commodity code partially matches the given search term.
     * A null or blank term does not apply any filtering.
     *
     * @param term The substring to search for within each item's commodity code.
     * @return This {@code RequisitionFilter} instance for method chaining.
     */
    public RequisitionFilter withItem(@Nullable String term) {
        if (term == null || term.isBlank()) return this;
        conditions.add(requisition -> requisition.getLineItems().stream()
                .map(LineItem::getItem)
                .anyMatch(item -> matchesCommodityCode(item, term)));
        return this;
    }

    /**
     * Adds a filter condition that matches requisitions containing a destination whose
     * location code partially matches the given search term.
     * A null or blank term does not apply any filtering.
     *
     * @param term The substring to search for in the destination's code.
     * @return This {@code RequisitionFilter} instance for method chaining.
     */
    public RequisitionFilter withLocation(@Nullable String term) {
        if (term == null || term.isBlank()) return this;
        conditions.add(Requisition ->
                Requisition.getDestination().getLocId().code().toLowerCase().contains(term.toLowerCase()));
        return this;
    }

    /**
     * Applies all configured filter conditions to the provided list of requisitions.
     * Only requisitions that satisfy all conditions will be included in the result.
     * If no filter conditions have been configured, all requisitions are returned unfiltered.
     *
     * @param requisitions The list of requisitions to filter.
     * @return A list of requisitions that match all filter conditions.
     */
    public List<Requisition> apply(@NotNull List<Requisition> requisitions) {
        return requisitions.stream()
                .filter(conditions.stream().reduce(x -> true, Predicate::and))
                .collect(Collectors.toList());
    }
}
