package gov.nysenate.ess.travel.accommodation;

import gov.nysenate.ess.travel.meal.MealTier;
import gov.nysenate.ess.travel.utils.Dollars;

import java.time.LocalDate;
import java.util.Objects;

/**
 * An Accommodation contains a DayStay for each day at an address.
 * The DayStay contains all info related to meal allowances for that address and date.
 */
public class Day implements Comparable<Day> {

    private final LocalDate date;
    private final MealTier tier;
    private boolean isMealsRequested;

    public Day(LocalDate date, MealTier tier, boolean isMealsRequested) {
        this.date = date;
        this.tier = tier;
        this.isMealsRequested = isMealsRequested;
    }

    public Dollars mealAllowance() {
        if (isMealsRequested()) {
            return tier.total();
        }
        else {
            return Dollars.ZERO;
        }
    }

    public LocalDate getDate() {
        return date;
    }

    protected MealTier getTier() {
        return tier;
    }

    protected boolean isMealsRequested() {
        return isMealsRequested;
    }

    protected void setMealsRequested(boolean isMealsRequested) {
        this.isMealsRequested = isMealsRequested;
    }

    @Override
    public String toString() {
        return "Day{" +
                "date=" + date +
                ", tier=" + tier +
                ", isMealsRequested=" + isMealsRequested +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Day day = (Day) o;
        return isMealsRequested == day.isMealsRequested &&
                Objects.equals(date, day.date) &&
                Objects.equals(tier, day.tier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, tier, isMealsRequested);
    }


    @Override
    public int compareTo(Day o) {
        int cmp = date.compareTo(o.date);
        if (cmp == 0) {
            cmp = tier.compareTo(o.tier);
            if (cmp == 0) {
                cmp = (isMealsRequested == o.isMealsRequested ? 0 : (isMealsRequested ? 1 : -1));
            }
        }
        return cmp;
    }
}
