package gov.nysenate.ess.travel.allowance.gsa.model;

import com.google.common.collect.ImmutableMap;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.Objects;

/**
 * Contains all tiers of meal reimbursement.
 * These tiers are effective between startDate and endDate.
 */
public class MealRate {

    // TODO do we need the id?
    private int id;
    /** The start date this meal rate is valid for. */
    private final LocalDate startDate;
    /** The end date this meal rate is valid for. Will be null if this is the currently valid MealRate. */
    private final LocalDate endDate;
    private final ImmutableMap<String, MealTier> tiers;

    public MealRate(LocalDate startDate, LocalDate endDate, Map<String, MealTier> tiers) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.tiers = ImmutableMap.copyOf(tiers);
    }

    /**
     * The senate allows reimbursement for breakfast and dinner
     * for every day of travel. Does not allow reimbursement for
     * lunch or incidental.
     * @param tier The meal tier use in the daily rate calculation.
     * @return
     */
    // TODO note, this may change, may need to separate breakfast and dinner values? maybe return MealTier?
    public BigDecimal getDailySenateRate(String tier) {
        MealTier mealTier = tiers.get(tier);
        return mealTier.getBreakfast().add(mealTier.getDinner());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MealRate mealRate = (MealRate) o;
        return id == mealRate.id &&
                Objects.equals(startDate, mealRate.startDate) &&
                Objects.equals(endDate, mealRate.endDate) &&
                Objects.equals(tiers, mealRate.tiers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, startDate, endDate, tiers);
    }
}
