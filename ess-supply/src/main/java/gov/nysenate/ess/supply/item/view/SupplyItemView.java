package gov.nysenate.ess.supply.item.view;

import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.supply.item.SupplyItem;

public class SupplyItemView implements ViewObject {

    protected int id;
    protected String commodityCode;
    protected String description;
    protected String unit;
    protected CategoryView category;
    protected int maxQtyPerOrder;
    protected int suggestedMaxQty;
    protected int standardQuantity;

    public SupplyItemView() {
    }

    public SupplyItemView(SupplyItem item) {
        this.id = item.getId();
        this.commodityCode = item.getCommodityCode();
        this.description = item.getDescription();
        this.unit = item.getUnit();
        this.category = new CategoryView(item.getCategory());
        this.maxQtyPerOrder = item.getMaxQtyPerOrder();
        this.suggestedMaxQty = item.getMaxQtyPerMonth();
        this.standardQuantity = item.getUnitStandardQuantity();
    }

    public SupplyItem toSupplyItem() {
        return new SupplyItem(id, commodityCode, description, unit, category.toCategory(), maxQtyPerOrder, suggestedMaxQty, standardQuantity);
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

    @Override
    public String getViewType() {
        return "Supply Item";
    }
}
