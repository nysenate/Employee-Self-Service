package gov.nysenate.ess.supply.reports.itemsummary;

import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.supply.requisition.view.RequisitionView;

public class ItemOccurrenceView implements ViewObject {
    private int itemId;
    private int quantity;
    private RequisitionView requisition;

    public ItemOccurrenceView() {
    }

    public ItemOccurrenceView(ItemOccurrence itemOccurrence) {
        this.itemId = itemOccurrence.getItemId();
        this.quantity = itemOccurrence.getQuantity();
        this.requisition = new RequisitionView(itemOccurrence.getRequisition());
    }

    public int getItemId() {
        return itemId;
    }

    public int getQuantity() {
        return quantity;
    }

    public RequisitionView getRequisition() {
        return requisition;
    }

    @Override
    public String getViewType() {
        return "item-occurrence";
    }
}
