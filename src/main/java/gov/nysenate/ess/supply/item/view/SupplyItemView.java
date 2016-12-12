package gov.nysenate.ess.supply.item.view;

import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.supply.allowance.ItemVisibility;
import gov.nysenate.ess.supply.item.model.*;

public class SupplyItemView implements ViewObject {

    protected int id;
    protected String commodityCode;
    protected String description;
    protected String unit;
    protected CategoryView category;
    protected int maxQtyPerOrder;
    protected int suggestedMaxQty;
    protected int standardQuantity;
    protected String visibility;
    protected boolean isInventoryTracked;
    protected boolean isExpendable;

    public SupplyItemView() {
    }

    public SupplyItemView(SupplyItem item) {
        this.id = item.getId();
        this.commodityCode = item.getCommodityCode();
        this.description = item.getDescription();
        this.unit = item.getUnitDescription();
        this.category = new CategoryView(item.getCategory());
        this.maxQtyPerOrder = item.getOrderMaxQty();
        this.suggestedMaxQty = item.getMonthlyMaxQty();
        this.standardQuantity = item.getUnitQuantity();
        this.visibility = item.getVisibility().toString();
        this.isInventoryTracked = item.requiresSynchronization();
        this.isExpendable = item.isExpendable();
    }

    public SupplyItem toSupplyItem() {
        return new SupplyItem.Builder()
                .withId(id)
                .withCommodityCode(commodityCode)
                .withDescription(description)
                .withStatus(new ItemStatus(isExpendable, isInventoryTracked))
                .withCategory(category.toCategory())
                .withAllowance(new ItemAllowance(maxQtyPerOrder, suggestedMaxQty))
                .withUnit(new ItemUnit(unit, standardQuantity))
                .withVisibility(ItemVisibility.valueOf(visibility))
                .build();
    }

    public int getId() {
        return id;
    }

    public String getCommodityCode() {
        return commodityCode;
    }

    public String getDescription() {
        return description;
    }

    public String getUnit() {
        return unit;
    }

    public CategoryView getCategory() {
        return category;
    }

    public int getMaxQtyPerOrder() {
        return maxQtyPerOrder;
    }

    public int getSuggestedMaxQty() {
        return suggestedMaxQty;
    }

    public int getStandardQuantity() {
        return standardQuantity;
    }

    public String getVisibility() {
        return visibility;
    }

    public boolean isInventoryTracked() {
        return isInventoryTracked;
    }

    @Override
    public String getViewType() {
        return "Supply Item";
    }
}
