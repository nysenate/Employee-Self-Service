package gov.nysenate.ess.supply.item.model;

public final class ItemStatus {

    /**
     * All items issued by supply are Expendable.
     */
    private final boolean isExpendable;

    /**
     * Is this item ordered by the supply department (Maintenance and Operations).
     * Items supply does not order do not have their inventories tracked in SFMS.
     */
    private final boolean orderedBySupply;

    public ItemStatus(boolean isExpendable, boolean orderedBySupply) {
        this.isExpendable = isExpendable;
        this.orderedBySupply = orderedBySupply;
    }

    public boolean isExpendable() {
        return isExpendable;
    }

    /**
     * Should this item be synchronized in SFMS.
     */
    public boolean requiresSynchronization() {
        return isExpendable && orderedBySupply;
    }

    @Override
    public String toString() {
        return "ItemStatus{" +
                "isExpendable=" + isExpendable +
                ", orderedBySupply=" + orderedBySupply +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemStatus that = (ItemStatus) o;
        if (isExpendable != that.isExpendable) return false;
        return orderedBySupply == that.orderedBySupply;
    }

    @Override
    public int hashCode() {
        int result = (isExpendable ? 1 : 0);
        result = 31 * result + (orderedBySupply ? 1 : 0);
        return result;
    }
}
