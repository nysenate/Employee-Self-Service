package gov.nysenate.ess.supply.reports.itemsummary;

import gov.nysenate.ess.supply.requisition.model.Requisition;

public class ItemOccurrence {
    private final int itemId;
    private final int quantity;
    private final Requisition requisition;

    public ItemOccurrence(int itemId, int quantity, Requisition requisition) {
        this.itemId = itemId;
        this.quantity = quantity;
        this.requisition = requisition;
    }

    public int getItemId() {
        return itemId;
    }

    public int getQuantity() {
        return quantity;
    }

    public Requisition getRequisition() {
        return requisition;
    }
}
