package gov.nysenate.ess.supply.sfms;

public final class SfmsLineItem {

    private int itemId;
    private int quantity;
    /**
     * The quantity of units issued multiplied by the quantity in each unit
     * example: An order of 1 box of pens with unit size 12/PKG, quantity = 1 and standard quantity = 12.
     */
    private int standardQuantity;
    private String unit;

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getStandardQuantity() {
        return standardQuantity;
    }

    public void setStandardQuantity(int standardQuantity) {
        this.standardQuantity = standardQuantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SfmsLineItem that = (SfmsLineItem) o;

        if (itemId != that.itemId) return false;
        if (quantity != that.quantity) return false;
        if (standardQuantity != that.standardQuantity) return false;
        return !(unit != null ? !unit.equals(that.unit) : that.unit != null);

    }

    @Override
    public int hashCode() {
        int result = itemId;
        result = 31 * result + quantity;
        result = 31 * result + standardQuantity;
        result = 31 * result + (unit != null ? unit.hashCode() : 0);
        return result;
    }
}
