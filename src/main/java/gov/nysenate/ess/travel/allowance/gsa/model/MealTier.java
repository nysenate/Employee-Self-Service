package gov.nysenate.ess.travel.allowance.gsa.model;

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
        this.breakfast = new BigDecimal(breakfast);
        this.lunch = new BigDecimal(lunch);
        this.dinner = new BigDecimal(dinner);
        this.incidental = new BigDecimal(incidental);
    }

    public String getTier() {
        return tier;
    }

    protected BigDecimal getBreakfast() {
        return breakfast;
    }

    protected BigDecimal getLunch() {
        return lunch;
    }

    protected BigDecimal getDinner() {
        return dinner;
    }

    protected BigDecimal getIncidental() {
        return incidental;
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