package gov.nysenate.ess.travel.request.allowances.meal;

import com.google.common.collect.ImmutableSortedSet;
import gov.nysenate.ess.travel.utils.Dollars;

import java.time.LocalDate;
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

    public MealPerDiems(Collection<MealPerDiem> mealPerDiems) {
        this(0, mealPerDiems, Dollars.ZERO);
    }

    public MealPerDiems(int id, Collection<MealPerDiem> mealPerDiems, Dollars overrideRate) {
        // If multiple MealPerDiem's for the same date, only keep the one with the highest rate.
        Map<LocalDate, MealPerDiem> dateToPerDiems = new HashMap<>();
        for (MealPerDiem mpd : mealPerDiems) {
            if (dateToPerDiems.containsKey(mpd.date())) {
                // Replace if this rate is higher.
                if (mpd.rate().compareTo(dateToPerDiems.get(mpd.date()).rate()) > 0) {
                    dateToPerDiems.put(mpd.date(), mpd);
                }
            }
            else {
                dateToPerDiems.put(mpd.date(), mpd);
            }
        }

        this.id = id;
        this.mealPerDiems = ImmutableSortedSet
                .orderedBy(dateComparator)
                .addAll(dateToPerDiems.values())
                .build();
        this.overrideRate = overrideRate ==  null ? Dollars.ZERO : overrideRate;
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

    public Dollars totalPerDiem() {
        return isOverridden() ?
                overrideRate
                : requestedMealPerDiems().stream()
                .map(MealPerDiem::requestedPerDiem)
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
        MealPerDiems that = (MealPerDiems) o;
        return Objects.equals(mealPerDiems, that.mealPerDiems);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mealPerDiems);
    }
}
