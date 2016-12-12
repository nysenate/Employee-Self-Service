package gov.nysenate.ess.supply.allowance;

import gov.nysenate.ess.core.model.unit.Location;
import gov.nysenate.ess.supply.item.model.SupplyItem;

/**
 * Contains a {@link SupplyItem} and item meta data specific to a {@link Location}.
 * An item's visibility, order quantities, and quantity ordered month-to-date
 * is location specific and stored in this object.
 */
public class ItemAllowance {

    private SupplyItem supplyItem;
    private ItemVisibility visibility;
    private int perOrderAllowance;
    private int perMonthAllowance;
    private int qtyOrderedMonthToDate;

    /**
     * @return The remaining quantity allowed to be ordered this month.
     * Always returns zero if monthly allowance has been exceeded.
     */
    public int getRemainingMonthlyAllowance() {
        int remaining = perMonthAllowance - qtyOrderedMonthToDate;
        return remaining < 0 ? 0 : remaining;
    }

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

    public int getPerOrderAllowance() {
        return perOrderAllowance;
    }

    public void setPerOrderAllowance(int perOrderAllowance) {
        this.perOrderAllowance = perOrderAllowance;
    }

    public int getPerMonthAllowance() {
        return perMonthAllowance;
    }

    public void setPerMonthAllowance(int perMonthAllowance) {
        this.perMonthAllowance = perMonthAllowance;
    }

    public int getQtyOrderedMonthToDate() {
        return qtyOrderedMonthToDate;
    }

    public void setQtyOrderedMonthToDate(int qtyOrderedMonthToDate) {
        this.qtyOrderedMonthToDate = qtyOrderedMonthToDate;
    }
}
