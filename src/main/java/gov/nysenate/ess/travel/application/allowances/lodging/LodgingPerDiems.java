package gov.nysenate.ess.travel.application.allowances.lodging;

import com.google.common.collect.ImmutableSortedSet;
import gov.nysenate.ess.travel.utils.Dollars;

import java.util.Collection;
import java.util.Comparator;
import java.util.Objects;

public class LodgingPerDiems {

    private final static Comparator<LodgingPerDiem> dateComparator = Comparator.comparing(LodgingPerDiem::date);
    private final ImmutableSortedSet<LodgingPerDiem> lodgingPerDiems;
    private Dollars overrideRate;

    public LodgingPerDiems(Collection<LodgingPerDiem> lodgingPerDiems) {
        this(lodgingPerDiems, Dollars.ZERO);
    }

    public LodgingPerDiems(Collection<LodgingPerDiem> lodgingPerDiems, Dollars overrideRate) {
        this.lodgingPerDiems = ImmutableSortedSet
                .orderedBy(dateComparator)
                .addAll(lodgingPerDiems)
                .build();
        this.overrideRate = overrideRate;
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
