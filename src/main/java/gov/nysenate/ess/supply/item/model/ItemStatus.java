package gov.nysenate.ess.supply.item.model;

public final class ItemStatus {

    /** All items issued by supply are Expendable. */
    private final boolean isExpendable;

    /**
     * Is this item made by the senate.
     *
     * If true, this item does not need to be synchronized. Inventory counts are not tracked
     * for items made by the senate.
     * If false, this item needs to by synchronized.
     *
     * Examples: Commodity codes: PML, PM, SL
     *
     * This is determined by the column "cdsensuppieditem" in SFMS.
     */
    private final boolean isMadeBySenate;

    /** Are non supply staff able to view and order this item. */
    private final boolean isVisible;

    /** Does this item require manager approval to be ordered. */
    private final boolean isSpecialRequest;

    public ItemStatus(boolean isExpendable, boolean isMadeBySenate,
                      boolean isVisible, boolean isSpecialRequest) {
        this.isExpendable = isExpendable;
        this.isMadeBySenate = isMadeBySenate;
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
        return isExpendable && !isMadeBySenate;
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
                ", isMadeBySenate=" + isMadeBySenate +
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
        if (isMadeBySenate != that.isMadeBySenate) return false;
        if (isVisible != that.isVisible) return false;
        return isSpecialRequest == that.isSpecialRequest;
    }

    @Override
    public int hashCode() {
        int result = (isExpendable ? 1 : 0);
        result = 31 * result + (isMadeBySenate ? 1 : 0);
        result = 31 * result + (isVisible ? 1 : 0);
        result = 31 * result + (isSpecialRequest ? 1 : 0);
        return result;
    }
}
