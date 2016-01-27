package gov.nysenate.ess.supply.item.view;

import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.supply.item.SupplyItem;

public final class SupplyItemView implements ViewObject {

    protected final int id;
    protected final String commodityCode;
    protected final String name;
    protected final String description;
    protected final String unit;
    protected final String category;
    protected final int suggestedMaxQty;
    protected final int standardQuantity;

    public SupplyItemView(SupplyItem item) {
        this.id = item.getId();
        this.commodityCode = item.getCommodityCode();
        this.name = item.getName();
        this.description = item.getDescription();
        this.unit = item.getUnit();
        this.category = item.getCategory();
        this.suggestedMaxQty = item.getSuggestedMaxQty();
        this.standardQuantity = item.getStandardQuantity();
    }

    public SupplyItem toSupplyItem() {
        return new SupplyItem(id, commodityCode, name, description, unit, category, suggestedMaxQty, standardQuantity);
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

    public String getCategory() {
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
