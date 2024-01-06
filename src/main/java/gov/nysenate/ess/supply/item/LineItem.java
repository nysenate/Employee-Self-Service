package gov.nysenate.ess.supply.item;

import gov.nysenate.ess.supply.item.model.SupplyItem;

import java.util.Objects;

/** A Line item represents an item in an order.
 * It contains the an item id and the quantity ordered. */
public final class LineItem {

    private final SupplyItem item;
    private final int quantity;

    public LineItem(SupplyItem item, int quantity) {
        this.item = item;
        this.quantity = quantity;
    }

    public SupplyItem getItem() {
        return item;
    }

    public int getQuantity() {
        return quantity;
    }

    @Override
    public String toString() {
        return "LineItem{" +
               "item=" + item +
               ", quantity=" + quantity +
               '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LineItem lineItem = (LineItem) o;
        return quantity == lineItem.quantity && Objects.equals(item, lineItem.item);

    }

    @Override
    public int hashCode() {
        int result = item != null ? item.hashCode() : 0;
        result = 31 * result + quantity;
        return result;
    }
}
