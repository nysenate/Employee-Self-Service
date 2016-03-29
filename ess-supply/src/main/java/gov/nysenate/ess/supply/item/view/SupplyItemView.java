package gov.nysenate.ess.supply.item.view;

import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.supply.item.SupplyItem;

public class SupplyItemView implements ViewObject {

    protected int id;
    protected String commodityCode;
    protected String name;
    protected String description;
    protected String unit;
    protected CategoryView category;
    protected int suggestedMaxQty;
    protected int standardQuantity;

    public SupplyItemView() {
    }

    public SupplyItemView(SupplyItem item) {
        this.id = item.getId();
        this.commodityCode = item.getCommodityCode();
        this.name = item.getName();
        this.description = item.getDescription();
        this.unit = item.getUnit();
        this.category = new CategoryView(item.getCategory());
        this.suggestedMaxQty = item.getSuggestedMaxQty();
        this.standardQuantity = item.getUnitStandardQuantity();
    }

    public SupplyItem toSupplyItem() {
        return new SupplyItem(id, commodityCode, name, description, unit, category.toCategory(), suggestedMaxQty, standardQuantity);
    }

    public int getId() {
        return id;
    }

    public String getCommodityCode() {
        return commodityCode;
    }

    public String getName() {
        return name;
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
