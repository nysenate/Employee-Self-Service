package gov.nysenate.ess.travel.request.allowances.meal;

import com.google.common.collect.ImmutableSortedSet;
import gov.nysenate.ess.travel.utils.Dollars;

import java.util.*;

/**
 * A collection of MealPerDiem's for a Travel Application Amendment.
 * Only uses the highest rate MealPerDiem for each day.
 * <p>
 * If these per diems are overridden, {@code overrideRate} will be non zero.
 */
public class MealPerDiems {

    private final static Comparator<MealPerDiem> dateComparator = Comparator.comparing(MealPerDiem::date);

    private final ImmutableSortedSet<MealPerDiem> mealPerDiems;
    private MealPerDiemAdjustments adjustments;

    public MealPerDiems(Collection<MealPerDiem> mealPerDiems) {
        this(mealPerDiems, new MealPerDiemAdjustments.Builder().build());
    }

    public MealPerDiems(Collection<MealPerDiem> mealPerDiems, MealPerDiemAdjustments adjustments) {
        this.mealPerDiems = ImmutableSortedSet
                .orderedBy(dateComparator)
                .addAll(mealPerDiems)
                .build();
        this.adjustments = adjustments == null
                ? new MealPerDiemAdjustments.Builder().build()
                : adjustments;
    }

    public Dollars total() {
        if (!adjustments.isAllowedMeals()) {
            return Dollars.ZERO;
        }
        return adjustments.isOverridden()
                ? adjustments.overrideRate()
                : requestedMealPerDiems().stream()
                .map(MealPerDiem::total)
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

    public boolean isOverridden() {
        return this.adjustments.isOverridden();
    }

    public Dollars overrideRate() {
        return this.adjustments.overrideRate();
    }

    public boolean isAllowedMeals() {
        return this.adjustments.isAllowedMeals();
    }

    protected MealPerDiemAdjustments getAdjustments() {
        return this.adjustments;
    }

    @Override
    public String toString() {
        return "MealPerDiems{" +
                "mealPerDiems=" + mealPerDiems +
                ", adjustments=" + adjustments +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MealPerDiems that = (MealPerDiems) o;
        return Objects.equals(mealPerDiems, that.mealPerDiems)
                && Objects.equals(adjustments, that.adjustments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mealPerDiems, adjustments);
    }
}
