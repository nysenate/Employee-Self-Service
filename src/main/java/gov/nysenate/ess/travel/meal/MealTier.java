package gov.nysenate.ess.travel.meal;

import gov.nysenate.ess.travel.Dollars;

import java.util.Objects;

/**
 * Represents a single row in the GSA Meals and Incidental Expenses breakdown
 * available at http://www.gsa.gov/mie.
 */
public class MealTier {

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
     * The Senate allows reimbursement only for breakfast and dinner.
     * @return the Senate provided meal allowance for this meal tier.
     */
    public Dollars allowance() {
        return getBreakfast().add(getDinner());
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
}