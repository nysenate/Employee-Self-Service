package gov.nysenate.ess.supply.allowance;

import gov.nysenate.ess.core.model.unit.Location;
import gov.nysenate.ess.supply.item.SupplyItem;

/**
 * Contains a {@link SupplyItem} and item meta data specific to a {@link Location}.
 * An item's visibility, order quantities, and quantity ordered month-to-date
 * is location specific and stored in this object.
 */
public class ItemAllowance {

    private SupplyItem supplyItem;
    private ItemVisibility visibility;
    private int maxQtyPerOrder;
    private int maxQtyPerMonth;
    private int qtyOrderedMonthToDate;

    public SupplyItem getSupplyItem() {
        return supplyItem;
    }

    public void setSupplyItem(SupplyItem supplyItem) {
        this.supplyItem = supplyItem;
    }

    public ItemVisibility getVisibility() {
        return visibility;
    }

    public void setVisibility(ItemVisibility visibility) {
        this.visibility = visibility;
    }

    public int getMaxQtyPerOrder() {
        return maxQtyPerOrder;
    }

    public void setMaxQtyPerOrder(int maxQtyPerOrder) {
        this.maxQtyPerOrder = maxQtyPerOrder;
    }

    public int getMaxQtyPerMonth() {
        return maxQtyPerMonth;
    }

    public void setMaxQtyPerMonth(int maxQtyPerMonth) {
        this.maxQtyPerMonth = maxQtyPerMonth;
    }

    public int getQtyOrderedMonthToDate() {
        return qtyOrderedMonthToDate;
    }

    public void setQtyOrderedMonthToDate(int qtyOrderedMonthToDate) {
        this.qtyOrderedMonthToDate = qtyOrderedMonthToDate;
    }

    @Override
    public String toString() {
        return "ItemAllowance{" +
               "supplyItem=" + supplyItem +
               ", visibility=" + visibility +
               ", maxQtyPerOrder=" + maxQtyPerOrder +
               ", maxQtyPerMonth=" + maxQtyPerMonth +
               ", qtyOrderedMonthToDate=" + qtyOrderedMonthToDate +
               '}';
    }
}
