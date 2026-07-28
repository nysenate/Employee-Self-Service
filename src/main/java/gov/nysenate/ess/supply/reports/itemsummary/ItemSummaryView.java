package gov.nysenate.ess.supply.reports.itemsummary;

import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.supply.item.view.SupplyItemView;

import java.util.List;
import java.util.stream.Collectors;

public class ItemSummaryView implements ViewObject {
    private SupplyItemView item;
    private int totalQuantity;
    private List<ItemOccurrenceView> occurrences;

    public ItemSummaryView() {
    }

    public ItemSummaryView(ItemSummary summary) {
        this.item = new SupplyItemView(summary.getItem());
        this.totalQuantity = summary.getTotalQuantity();
        this.occurrences = summary.getOccurrences().stream()
                .map(ItemOccurrenceView::new)
                .collect(Collectors.toList());
    }

    public SupplyItemView getItem() {
        return item;
    }

    public int getTotalQuantity() {
        return totalQuantity;
    }

    public List<ItemOccurrenceView> getOccurrences() {
        return occurrences;
    }

    @Override
    public String getViewType() {
        return "item-summary";
    }
}
