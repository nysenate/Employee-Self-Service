package gov.nysenate.ess.travel.request.allowances.meal;

import com.google.common.collect.ImmutableSortedSet;
import gov.nysenate.ess.travel.utils.Dollars;

import java.util.*;

/**
 * A collection of MealPerDiem's for a Travel Application Amendment.
 * Only uses the highest rate MealPerDiem for each day.
 *
 * If these per diems are overridden, {@code overrideRate} will be non zero.
 */
public class MealPerDiems {

    private final static Comparator<MealPerDiem> dateComparator = Comparator.comparing(MealPerDiem::date);

    private int id;
    private final ImmutableSortedSet<MealPerDiem> mealPerDiems;
    private Dollars overrideRate;
    private final boolean isAllowedMeals;

    public MealPerDiems(Collection<MealPerDiem> mealPerDiems) {
        this(0, mealPerDiems, Dollars.ZERO, true);
    }

    public MealPerDiems(Collection<MealPerDiem> mealPerDiems, boolean isAllowedMeals) {
        this(0, mealPerDiems, Dollars.ZERO, isAllowedMeals);
    }

    public MealPerDiems(int id, Collection<MealPerDiem> mealPerDiems, Dollars overrideRate, boolean isAllowedMeals) {
        this.id = id;
        this.mealPerDiems = ImmutableSortedSet
                .orderedBy(dateComparator)
                .addAll(mealPerDiems)
                .build();
        this.overrideRate = overrideRate == null ? Dollars.ZERO : overrideRate;
        this.isAllowedMeals = isAllowedMeals;
    }

    public Dollars total() {
        if (!isAllowedMeals) {
            return Dollars.ZERO;
        }
        return isOverridden() ?
                overrideRate
                : requestedMealPerDiems().stream()
                .map(MealPerDiem::total)
                .reduce(Dollars.ZERO, Dollars::add);
    }

    public int id() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setOverrideRate(Dollars rate) {
        this.overrideRate = rate;
    }

    public Dollars overrideRate() {
        return this.overrideRate;
    }

    public boolean isOverridden() {
        return !overrideRate.equals(Dollars.ZERO);
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

    public boolean isAllowedMeals() {
        return isAllowedMeals;
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
        MealPerDiems that = (MealPerDiems) o;
        return Objects.equals(mealPerDiems, that.mealPerDiems);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mealPerDiems);
    }
}
