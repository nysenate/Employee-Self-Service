package gov.nysenate.ess.supply.item;

/** A Line item represents an item in an order.
 * It contains the an item id and the quantity ordered. */
public final class LineItem {

    private final SupplyItem item;
    private final int quantity;

    public LineItem(SupplyItem itemId, int quantity) {
        this.item = itemId;
        this.quantity = quantity;
    }

    public SupplyItem getItem() {
        return item;
    }

    public int getQuantity() {
        return quantity;
    }
}
