package gov.nysenate.ess.travel.request.allowances.lodging;

import com.google.common.collect.ImmutableSortedSet;
import gov.nysenate.ess.travel.utils.Dollars;

import java.time.LocalDate;
import java.util.*;

public class LodgingPerDiems {

    private final static Comparator<LodgingPerDiem> dateComparator = Comparator.comparing(LodgingPerDiem::date);

    private final ImmutableSortedSet<LodgingPerDiem> lodgingPerDiems;
    private Dollars overrideRate;

    public LodgingPerDiems(Collection<LodgingPerDiem> lodgingPerDiems) {
        this(lodgingPerDiems, Dollars.ZERO);
    }

    public LodgingPerDiems(Collection<LodgingPerDiem> lodgingPerDiems, Dollars overrideRate) {
        // If multiple LodgingPerDiem's for the same date, only keep the one with the highest rate.
        Map<LocalDate, LodgingPerDiem> dateToPerDiems = new HashMap<>();
        for (LodgingPerDiem lpd : lodgingPerDiems) {
            if (dateToPerDiems.containsKey(lpd.date())) {
                // Replace if this rate is higher.
                if (lpd.rate().compareTo(dateToPerDiems.get(lpd.date()).rate()) > 0) {
                    dateToPerDiems.put(lpd.date(), lpd);
                }
            }
            else {
                dateToPerDiems.put(lpd.date(), lpd);
            }
        }
        this.lodgingPerDiems = ImmutableSortedSet
                .orderedBy(dateComparator)
                .addAll(dateToPerDiems.values())
                .build();
        this.overrideRate = overrideRate == null ? Dollars.ZERO : overrideRate;
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
                : requestedLodgingPerDiems().stream()
                .map(LodgingPerDiem::requestedPerDiem)
                .reduce(Dollars.ZERO, Dollars::add);
    }

    public ImmutableSortedSet<LodgingPerDiem> allLodgingPerDiems() {
        return lodgingPerDiems;
    }

    public ImmutableSortedSet<LodgingPerDiem> requestedLodgingPerDiems() {
        return lodgingPerDiems.stream()
                .filter(LodgingPerDiem::isReimbursementRequested)
                .collect(ImmutableSortedSet.toImmutableSortedSet(dateComparator));
    }


    @Override
    public String toString() {
        return "LodgingAllowances{" +
                "lodgingPerDiems=" + lodgingPerDiems +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LodgingPerDiems that = (LodgingPerDiems) o;
        return Objects.equals(lodgingPerDiems, that.lodgingPerDiems);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lodgingPerDiems);
    }
}
