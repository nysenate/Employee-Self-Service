package gov.nysenate.ess.travel.provider.gsa.meal;

import gov.nysenate.ess.travel.utils.Dollars;

import java.util.Objects;

/**
 * Represents a single row in the GSA Meals and Incidental Expenses breakdown
 * available at http://www.gsa.gov/mie.
 */
public class MealTier implements Comparable<MealTier> {

    private final String tier; // Also known as M&IE Total.
    private final Dollars total;
    private final Dollars incidental;

    public MealTier(String tier, String breakfast, String lunch, String dinner, String incidental) {
        this.tier = tier;
        this.total = new Dollars(breakfast).add(new Dollars(lunch)).add(new Dollars(dinner));
        this.incidental = new Dollars(incidental);
    }

    public MealTier(String tier, String total, String incidental) {
        this.tier = tier;
        this.total = new Dollars(total);
        this.incidental = new Dollars(incidental);
    }

    /**
     * The senate only allows reimbursement for breakfast and dinner, however the amount reimbursed
     * for each of those meals is different then GSA. The senate gives more for those meals such that
     * the GSA total and senate total is the same. Therefore we use the GSA total as the total meal reimbursement.
     * @return the Senate provided meal allowance for this meal tier.
     */
    public Dollars total() {
        return total.add(getIncidental());
    }

    protected String getTier() {
        return tier;
    }

    protected Dollars getIncidental() {
        return incidental;
    }

    protected Dollars getTotal() {
        return total;
    }

    @Override
    public String toString() {
        return "MealTier{" +
                "tier='" + tier + '\'' +
                ", total=" + total +
                ", incidental=" + incidental +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MealTier tier1 = (MealTier) o;
        return Objects.equals(tier, tier1.tier) &&
                Objects.equals(total, tier1.total) &&
                Objects.equals(incidental, tier1.incidental);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tier, total, incidental);
    }

    @Override
    public int compareTo(MealTier o) {
        int cmp = tier.compareTo(o.tier);
        if (cmp == 0) {
            cmp = total.compareTo(o.total);
            if (cmp == 0) {
                cmp = incidental.compareTo(o.incidental);
            }
        }
        return cmp;
    }
}