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
}
