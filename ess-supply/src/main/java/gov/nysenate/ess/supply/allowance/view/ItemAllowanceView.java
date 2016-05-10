package gov.nysenate.ess.supply.allowance.view;

import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.supply.allowance.ItemAllowance;
import gov.nysenate.ess.supply.allowance.ItemVisibility;
import gov.nysenate.ess.supply.item.view.SupplyItemView;

public class ItemAllowanceView implements ViewObject {

    protected SupplyItemView item;
    protected String visibility;
    protected int maxQtyPerOrder;
    protected int maxQtyPerMonth;
    protected int qtyOrderedMonthToDate;

    public ItemAllowanceView(ItemAllowance itemAllowance) {
        this.item = new SupplyItemView(itemAllowance.getSupplyItem());
        this.visibility = itemAllowance.getVisibility().toString();
        this.maxQtyPerOrder = itemAllowance.getMaxQtyPerOrder();
        this.maxQtyPerMonth = itemAllowance.getMaxQtyPerMonth();
        this.qtyOrderedMonthToDate = itemAllowance.getQtyOrderedMonthToDate();
    }

    public ItemAllowance toItemAllowance() {
        ItemAllowance allowance = new ItemAllowance();
        allowance.setSupplyItem(item.toSupplyItem());
        allowance.setVisibility(ItemVisibility.valueOf(visibility));
        allowance.setMaxQtyPerOrder(maxQtyPerOrder);
        allowance.setMaxQtyPerMonth(maxQtyPerMonth);
        allowance.setQtyOrderedMonthToDate(qtyOrderedMonthToDate);
        return allowance;
    }

    public SupplyItemView getItem() {
        return item;
    }

    public String getVisibility() {
        return visibility;
    }

    public int getMaxQtyPerOrder() {
        return maxQtyPerOrder;
    }

    public int getMaxQtyPerMonth() {
        return maxQtyPerMonth;
    }

    public int getQtyOrderedMonthToDate() {
        return qtyOrderedMonthToDate;
    }

    @Override
    public String getViewType() {
        return "item-allowance";
    }
}
