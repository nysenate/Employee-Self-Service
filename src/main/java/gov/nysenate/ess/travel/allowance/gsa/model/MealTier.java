package gov.nysenate.ess.travel.allowance.gsa.model;

import gov.nysenate.ess.travel.utils.UnitUtils;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Represents a single row in the GSA Meals and Incidental Expenses breakdown
 * available at http://www.gsa.gov/mie.
 *
 * For simplicity, values are stored as text in the database and converted
 * into BigDecimal's in the MealTier class.
 */
public class MealTier {

    private final String tier; // Also known as M&IE Total.
    private final BigDecimal breakfast;
    private final BigDecimal lunch;
    private final BigDecimal dinner;
    private final BigDecimal incidental;

    public MealTier(String tier, String breakfast, String lunch, String dinner, String incidental) {
        this.tier = tier;
        this.breakfast = UnitUtils.roundToHundredth(new BigDecimal(breakfast));
        this.lunch = UnitUtils.roundToHundredth(new BigDecimal(lunch));
        this.dinner = UnitUtils.roundToHundredth(new BigDecimal(dinner));
        this.incidental = UnitUtils.roundToHundredth(new BigDecimal(incidental));
    }

    public String getTier() {
        return tier;
    }

    public BigDecimal getBreakfast() {
        return breakfast;
    }

    public BigDecimal getLunch() {
        return lunch;
    }

    public BigDecimal getDinner() {
        return dinner;
    }

    public BigDecimal getIncidental() {
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
        MealTier mealTier = (MealTier) o;
        return Objects.equals(tier, mealTier.tier) &&
                Objects.equals(breakfast, mealTier.breakfast) &&
                Objects.equals(lunch, mealTier.lunch) &&
                Objects.equals(dinner, mealTier.dinner) &&
                Objects.equals(incidental, mealTier.incidental);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tier, breakfast, lunch, dinner, incidental);
    }
}