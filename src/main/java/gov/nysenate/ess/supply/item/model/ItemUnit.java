package gov.nysenate.ess.supply.item.model;

public final class ItemUnit {

    /**
     * A description of the unit. e.g 12/PKG
     */
    private final String description;

    /**
     * The quantity of the unit.
     * e.g. A unit with a description of "12/PKG" would have a quantity of 12.
     */
    private final int quantity;

    public ItemUnit(String description, int quantity) {
        this.description = description;
        this.quantity = quantity;
    }

    String getDescription() {
        return description;
    }

    int getQuantity() {
        return quantity;
    }

    @Override
    public String toString() {
        return "ItemUnit{" +
                "description='" + description + '\'' +
                ", quantity=" + quantity +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemUnit itemUnit = (ItemUnit) o;
        if (quantity != itemUnit.quantity) return false;
        return description != null ? description.equals(itemUnit.description) : itemUnit.description == null;
    }

    @Override
    public int hashCode() {
        int result = description != null ? description.hashCode() : 0;
        result = 31 * result + quantity;
        return result;
    }
}
