package gov.nysenate.ess.travel.accommodation;

import gov.nysenate.ess.travel.meal.MealTier;
import gov.nysenate.ess.travel.utils.Dollars;

import java.time.LocalDate;
import java.util.Objects;

/**
 * An Accommodation contains a DayStay for each day at an address.
 * The DayStay contains all info related to meal allowances for that address and date.
 */
public class DayStay extends Stay {

    private final MealTier tier;

    public DayStay(LocalDate date, MealTier tier) {
        super(date);
        this.tier = tier;
    }

    @Override
    public Dollars mealAllowance() {
        return tier.allowance();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        DayStay dayStay = (DayStay) o;
        return Objects.equals(tier, dayStay.tier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), tier);
    }
}
