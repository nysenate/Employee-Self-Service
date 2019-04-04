package gov.nysenate.ess.travel.application.allowances.lodging;

import com.google.common.collect.ImmutableSortedSet;
import gov.nysenate.ess.travel.utils.Dollars;

import java.util.Collection;
import java.util.Comparator;
import java.util.Objects;

public class LodgingAllowances {

    private final static Comparator<LodgingPerDiem> dateComparator = Comparator.comparing(LodgingPerDiem::getDate);
    private final ImmutableSortedSet<LodgingPerDiem> lodgingPerDiems;

    public LodgingAllowances(Collection<LodgingPerDiem> lodgingPerDiems) {
        this.lodgingPerDiems = ImmutableSortedSet
                .orderedBy(dateComparator)
                .addAll(lodgingPerDiems)
                .build();
    }

    public Dollars totalRequestedAllowance() {
        return allLodgingPerDiems().stream()
                .map(LodgingPerDiem::totalRequestedAllowance)
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
        LodgingAllowances that = (LodgingAllowances) o;
        return Objects.equals(lodgingPerDiems, that.lodgingPerDiems);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lodgingPerDiems);
    }
}
