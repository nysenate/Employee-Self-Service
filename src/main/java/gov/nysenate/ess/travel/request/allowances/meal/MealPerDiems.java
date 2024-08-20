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
    // Sorts MealPerDiem's by date.
    private final static Comparator<MealPerDiem> dateComparator = Comparator.comparing(MealPerDiem::date);

    private final ImmutableSortedSet<MealPerDiem> mealPerDiems;
    private MealPerDiemAdjustments adjustments;

    public MealPerDiems(Collection<MealPerDiem> mealPerDiems) {
        this(mealPerDiems, new MealPerDiemAdjustments.Builder().build());
    }

    public MealPerDiems(Collection<MealPerDiem> mealPerDiems, MealPerDiemAdjustments adjustments) {
        if (mealPerDiems == null) {
            mealPerDiems = ImmutableSortedSet.of();
        }
        this.mealPerDiems = ImmutableSortedSet
                .orderedBy(dateComparator)
                .addAll(mealPerDiems)
                .build();
        this.adjustments = adjustments == null
                ? new MealPerDiemAdjustments.Builder().build()
                : adjustments;
    }

    public boolean isAllowedMeals() {
        return this.adjustments.isAllowedMeals();
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
     * @return Only the requested meal per diems.
     */
    public ImmutableSortedSet<MealPerDiem> requestedMealPerDiems() {
        return mealPerDiems.stream()
                .filter(mpd -> mpd.isBreakfastRequested() || mpd.isDinnerRequested())
                .collect(ImmutableSortedSet.toImmutableSortedSet(dateComparator));
    }

    protected ImmutableSortedSet<MealPerDiem> allMealPerDiems() {
        return mealPerDiems;
    }

    protected boolean isOverridden() {
        return this.adjustments.isOverridden();
    }

    protected Dollars overrideRate() {
        return this.adjustments.overrideRate();
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
