package gov.nysenate.ess.travel.application.allowances.meal;

import com.google.common.collect.ImmutableList;
import gov.nysenate.ess.travel.utils.Dollars;

import java.util.List;
import java.util.Objects;

public class MealAllowances {

    private final ImmutableList<MealAllowance> mealAllowances;

    public MealAllowances(List<MealAllowance> mealAllowances) {
        this.mealAllowances = ImmutableList.copyOf(mealAllowances);
    }

    public Dollars totalAllowance() {
        return getMealAllowances().stream()
                .map(MealAllowance::allowance)
                .reduce(Dollars.ZERO, Dollars::add);
    }

    protected ImmutableList<MealAllowance> getMealAllowances() {
        return mealAllowances;
    }

    @Override
    public String toString() {
        return "MealAllowance{" +
                "mealAllowances=" + mealAllowances +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MealAllowances that = (MealAllowances) o;
        return Objects.equals(mealAllowances, that.mealAllowances);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mealAllowances);
    }
}
