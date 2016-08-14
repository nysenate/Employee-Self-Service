package gov.nysenate.ess.supply.allowance.view;

import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.supply.allowance.ItemAllowance;
import gov.nysenate.ess.supply.allowance.ItemVisibility;
import gov.nysenate.ess.supply.item.view.SupplyItemView;

public class ItemAllowanceView implements ViewObject {

    protected SupplyItemView item;
    protected String visibility;
    protected int perOrderAllowance;
    protected int perMonthAllowance;
    protected int qtyOrderedMonthToDate;
    protected int remainingMonthlyAllowance;
    /**
     * Initialize the selected quantity for the order view page.
     * This is the default value to be selected in the drop down quantity selector for this item/allowance.
     * The Order page uses this value to track the quantity selected.
     */
    protected int selectedQuantity;

    public ItemAllowanceView(ItemAllowance itemAllowance) {
        this.item = new SupplyItemView(itemAllowance.getSupplyItem());
        this.visibility = itemAllowance.getVisibility().toString();
        this.perOrderAllowance = itemAllowance.getPerOrderAllowance();
        this.perMonthAllowance = itemAllowance.getPerMonthAllowance();
        this.qtyOrderedMonthToDate = itemAllowance.getQtyOrderedMonthToDate();
        this.remainingMonthlyAllowance = itemAllowance.getRemainingMonthlyAllowance();
        this.selectedQuantity = 1;
    }

    public ItemAllowance toItemAllowance() {
        ItemAllowance allowance = new ItemAllowance();
        allowance.setSupplyItem(item.toSupplyItem());
        allowance.setVisibility(ItemVisibility.valueOf(visibility));
        allowance.setPerOrderAllowance(perOrderAllowance);
        allowance.setPerMonthAllowance(perMonthAllowance);
        allowance.setQtyOrderedMonthToDate(qtyOrderedMonthToDate);
        return allowance;
    }

    public SupplyItemView getItem() {
        return item;
    }

    public String getVisibility() {
        return visibility;
    }

    public int getPerOrderAllowance() {
        return perOrderAllowance;
    }

    public int getPerMonthAllowance() {
        return perMonthAllowance;
    }

    public int getQtyOrderedMonthToDate() {
        return qtyOrderedMonthToDate;
    }

    public int getRemainingMonthlyAllowance() {
        return remainingMonthlyAllowance;
    }

    public int getSelectedQuantity() {
        return selectedQuantity;
    }

    @Override
    public String getViewType() {
        return "item-allowance";
    }
}
