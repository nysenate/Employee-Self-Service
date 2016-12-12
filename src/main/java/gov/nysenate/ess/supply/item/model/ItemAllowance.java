package gov.nysenate.ess.supply.item.model;

/**
 * Recommended item order quantities.
 */
public final class ItemAllowance {

    /** Recommended max quantity in a single requisition. */
    private final int perOrderAllowance;
    /** Recommended max quantity per month per location. */
    private final int perMonthAllowance;

    public ItemAllowance(int perOrderAllowance, int perMonthAllowance) {
        this.perOrderAllowance = perOrderAllowance;
        this.perMonthAllowance = perMonthAllowance;
    }

    int getPerOrderAllowance() {
        return perOrderAllowance;
    }

    int getPerMonthAllowance() {
        return perMonthAllowance;
    }

    @Override
    public String toString() {
        return "ItemAllowance{" +
                "orderMax=" + perOrderAllowance +
                ", monthlyMax=" + perMonthAllowance +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemAllowance that = (ItemAllowance) o;
        if (perOrderAllowance != that.perOrderAllowance) return false;
        return perMonthAllowance == that.perMonthAllowance;
    }

    @Override
    public int hashCode() {
        int result = perOrderAllowance;
        result = 31 * result + perMonthAllowance;
        return result;
    }
}
