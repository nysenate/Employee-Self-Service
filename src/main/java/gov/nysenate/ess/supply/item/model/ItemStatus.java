package gov.nysenate.ess.supply.item.model;

public final class ItemStatus {

    /** All items issued by supply are Expendable. */
    private final boolean isExpendable;

    /**
     * Is this item ordered by the supply department (Maintenance and Operations).
     * Items supply does not order do not have their inventories tracked in SFMS.
     */
    private final boolean isOrderedBySupply;

    /** Are non supply staff able to view and order this item. */
    private final boolean isVisible;

    /** Does this item require manager approval to be ordered. */
    private final boolean isSpecialRequest;

    public ItemStatus(boolean isExpendable, boolean isOrderedBySupply,
                      boolean isVisible, boolean isSpecialRequest) {
        this.isExpendable = isExpendable;
        this.isOrderedBySupply = isOrderedBySupply;
        this.isVisible = isVisible;
        this.isSpecialRequest = isSpecialRequest;
    }

    boolean isExpendable() {
        return isExpendable;
    }

    /**
     * Should this item be synchronized in SFMS.
     */
    boolean requiresSynchronization() {
        return isExpendable && isOrderedBySupply;
    }

    boolean isVisible() {
        return isVisible;
    }

    boolean isSpecialRequest() {
        return isSpecialRequest;
    }

    @Override
    public String toString() {
        return "ItemStatus{" +
                "isExpendable=" + isExpendable +
                ", orderedBySupply=" + isOrderedBySupply +
                ", isVisible=" + isVisible +
                ", isSpecialRequest=" + isSpecialRequest +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemStatus that = (ItemStatus) o;
        if (isExpendable != that.isExpendable) return false;
        if (isOrderedBySupply != that.isOrderedBySupply) return false;
        if (isVisible != that.isVisible) return false;
        return isSpecialRequest == that.isSpecialRequest;
    }

    @Override
    public int hashCode() {
        int result = (isExpendable ? 1 : 0);
        result = 31 * result + (isOrderedBySupply ? 1 : 0);
        result = 31 * result + (isVisible ? 1 : 0);
        result = 31 * result + (isSpecialRequest ? 1 : 0);
        return result;
    }
}
