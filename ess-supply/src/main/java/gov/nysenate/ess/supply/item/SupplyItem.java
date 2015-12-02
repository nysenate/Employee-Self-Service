package gov.nysenate.ess.supply.item;

public final class SupplyItem {

    private final String commodityCode; // uniquely identifies items
    private final String name;
    private final String description;
    private final int unitsize;
    private final String category;
    private final int warnQuantity;

    public SupplyItem(String commodityCode, String name, String description, int unitsize,
                      String category, int warnQuantity) {
        this.commodityCode = commodityCode;
        this.name = name;
        this.description = description;
        this.unitsize = unitsize;
        this.category = category;
        this.warnQuantity = warnQuantity;
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

    public int getUnitsize() {
        return unitsize;
    }

    public String getCategory() {
        return category;
    }

    public int getWarnQuantity() {
        return warnQuantity;
    }
}
