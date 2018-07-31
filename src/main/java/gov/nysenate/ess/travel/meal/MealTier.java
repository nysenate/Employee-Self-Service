package gov.nysenate.ess.travel.meal;

import gov.nysenate.ess.travel.utils.Dollars;

import java.util.Objects;

/**
 * Represents a single row in the GSA Meals and Incidental Expenses breakdown
 * available at http://www.gsa.gov/mie.
 */
public class MealTier implements Comparable<MealTier> {

    private final String tier; // Also known as M&IE Total.
    private final Dollars breakfast;
    private final Dollars lunch;
    private final Dollars dinner;
    private final Dollars incidental;

    public MealTier(String tier, String breakfast, String lunch, String dinner, String incidental) {
        this.tier = tier;
        this.breakfast = new Dollars(breakfast);
        this.lunch = new Dollars(lunch);
        this.dinner = new Dollars(dinner);
        this.incidental = new Dollars(incidental);
    }

    /**
     * The senate only allows reimbursement for breakfast and dinner, however the amount reimbursed
     * for each of those meals is different then GSA. The senate gives more for those meals such that
     * the GSA total and senate total is the same. Therefore we use the GSA total as the total meal reimbursement.
     * @return the Senate provided meal allowance for this meal tier.
     */
    public Dollars allowance() {
        return getBreakfast().add(getLunch()).add(getDinner()).add(getIncidental());
    }

    protected String getTier() {
        return tier;
    }

    protected Dollars getBreakfast() {
        return breakfast;
    }

    protected Dollars getLunch() {
        return lunch;
    }

    protected Dollars getDinner() {
        return dinner;
    }

    protected Dollars getIncidental() {
        return incidental;
    }

    @Override
    public String toString() {
        return "MealTier{" +
                "tier='" + tier + '\'' +
                ", breakfast=" + breakfast +
                ", lunch=" + lunch +
                ", dinner=" + dinner +
                ", incidental=" + incidental +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MealTier tier1 = (MealTier) o;
        return Objects.equals(tier, tier1.tier) &&
                Objects.equals(breakfast, tier1.breakfast) &&
                Objects.equals(lunch, tier1.lunch) &&
                Objects.equals(dinner, tier1.dinner) &&
                Objects.equals(incidental, tier1.incidental);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tier, breakfast, lunch, dinner, incidental);
    }

    @Override
    public int compareTo(MealTier o) {
        int cmp = tier.compareTo(o.tier);
        if (cmp == 0) {
            cmp = breakfast.compareTo(o.breakfast);
            if (cmp == 0) {
                cmp = lunch.compareTo(o.lunch);
                if (cmp == 0) {
                    cmp = dinner.compareTo(o.dinner);
                    if (cmp == 0) {
                        cmp = incidental.compareTo(o.incidental);
                    }
                }
            }
        }
        return cmp;
    }
}