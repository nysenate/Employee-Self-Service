package gov.nysenate.ess.supply.item;

public final class SupplyItem {

    private final int id;
    private final String commodityCode;
    private final String description;
    private final String unit;
    private final Category category;
    private final int maxQtyPerOrder;
    private final int maxQtyPerMonth;
    /** Number of items per unit. eg. 12/PKG would equal 12 */
    private final int unitStandardQuantity;

    public SupplyItem(int id, String commodityCode, String description, String unit,
                      Category category, int maxQtyPerOrder, int maxQtyPerMonth, int unitStandardQuantity) {
        this.id = id;
        this.commodityCode = commodityCode;
        this.description = description;
        this.unit = unit;
        this.category = category;
        this.maxQtyPerOrder = maxQtyPerOrder;
        this.maxQtyPerMonth = maxQtyPerMonth;
        this.unitStandardQuantity = unitStandardQuantity;
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

    public Category getCategory() {
        return category;
    }

    public int getMaxQtyPerOrder() {
        return maxQtyPerOrder;
    }

    public int getMaxQtyPerMonth() {
        return maxQtyPerMonth;
    }

    public int getUnitStandardQuantity() {
        return unitStandardQuantity;
    }

    @Override
    public String toString() {
        return "SupplyItem{" +
               "id=" + id +
               ", commodityCode='" + commodityCode + '\'' +
               ", description='" + description + '\'' +
               ", unit='" + unit + '\'' +
               ", category=" + category +
               ", maxQtyPerOrder=" + maxQtyPerOrder +
               ", maxQtyPerMonth=" + maxQtyPerMonth +
               ", unitStandardQuantity=" + unitStandardQuantity +
               '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SupplyItem that = (SupplyItem) o;

        if (id != that.id) return false;
        if (maxQtyPerOrder != that.maxQtyPerOrder) return false;
        if (maxQtyPerMonth != that.maxQtyPerMonth) return false;
        if (unitStandardQuantity != that.unitStandardQuantity) return false;
        if (commodityCode != null ? !commodityCode.equals(that.commodityCode) : that.commodityCode != null)
            return false;
        if (description != null ? !description.equals(that.description) : that.description != null) return false;
        if (unit != null ? !unit.equals(that.unit) : that.unit != null) return false;
        return category != null ? category.equals(that.category) : that.category == null;

    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (commodityCode != null ? commodityCode.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (unit != null ? unit.hashCode() : 0);
        result = 31 * result + (category != null ? category.hashCode() : 0);
        result = 31 * result + maxQtyPerOrder;
        result = 31 * result + maxQtyPerMonth;
        result = 31 * result + unitStandardQuantity;
        return result;
    }
}
