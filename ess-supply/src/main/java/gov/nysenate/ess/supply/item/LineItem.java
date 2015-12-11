package gov.nysenate.ess.supply.item;

/** A Line item represents an item in an order.
 * It contains the an item id and the quantity ordered. */
public final class LineItem {

    private final int itemId;
    private final int quantity;

    public LineItem(int itemId, int quantity) {
        this.itemId = itemId;
        this.quantity = quantity;
    }

    public int getItemId() {
        return itemId;
    }

    public int getQuantity() {
        return quantity;
    }
}
