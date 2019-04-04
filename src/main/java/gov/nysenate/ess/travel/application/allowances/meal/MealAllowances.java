package gov.nysenate.ess.travel.application.allowances.meal;

import com.google.common.collect.ImmutableSortedSet;
import gov.nysenate.ess.travel.utils.Dollars;

import java.util.Collection;
import java.util.Comparator;
import java.util.Objects;

public class MealAllowances {

    private final static Comparator<MealPerDiem> dateComparator = Comparator.comparing(MealPerDiem::date);
    private final ImmutableSortedSet<MealPerDiem> mealPerDiems;

    public MealAllowances(Collection<MealPerDiem> mealPerDiems) {
        this.mealPerDiems = ImmutableSortedSet
                .orderedBy(dateComparator)
                .addAll(mealPerDiems)
                .build();
    }

    public Dollars maximumAllowance() {
        return allMealPerDiems().stream()
                .map(MealPerDiem::maximumAllowance)
                .reduce(Dollars.ZERO, Dollars::add);
    }

    public Dollars requestedAllowance() {
        return requestedMealPerDiems().stream()
                .map(MealPerDiem::requestedAllowance)
                .reduce(Dollars.ZERO, Dollars::add);
    }

    /**
     * @return All meal per diems.
     */
    public ImmutableSortedSet<MealPerDiem> allMealPerDiems() {
        return mealPerDiems;
    }

    /**
     * @return Only the requested meal per diems.
     */
    public ImmutableSortedSet<MealPerDiem> requestedMealPerDiems() {
        return mealPerDiems.stream()
                .filter(MealPerDiem::isReimbursementRequested)
                .collect(ImmutableSortedSet.toImmutableSortedSet(dateComparator));
    }


    @Override
    public String toString() {
        return "MealExpenses{" +
                "mealPerDiems=" + mealPerDiems +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MealAllowances that = (MealAllowances) o;
        return Objects.equals(mealPerDiems, that.mealPerDiems);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mealPerDiems);
    }
}
