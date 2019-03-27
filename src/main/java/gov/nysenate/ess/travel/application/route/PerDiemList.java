package gov.nysenate.ess.travel.application.route;

import com.google.common.collect.ImmutableSortedSet;
import gov.nysenate.ess.travel.utils.Dollars;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class PerDiemList {

    private final ImmutableSortedSet<PerDiem> perDiems;

    public PerDiemList(List<PerDiem> perDiems) {
        this.perDiems = ImmutableSortedSet
                .orderedBy(Comparator.comparing(PerDiem::getDate))
                .addAll(perDiems)
                .build();
    }

    public static PerDiemList of(List<PerDiem> perDiems) {
        return new PerDiemList(perDiems);
    }

    public static PerDiemList ofLists(List<PerDiemList> perDiemLists) {
        return new PerDiemList(perDiemLists.stream()
                .map(PerDiemList::getPerDiems)
                .flatMap(Collection::stream)
                .collect(Collectors.toList()));
    }

    public Dollars total() {
        return getPerDiems().stream()
                .map(PerDiem::getDollars)
                .reduce(Dollars.ZERO, Dollars::add);
    }

    public ImmutableSortedSet<PerDiem> getPerDiems() {
        return perDiems;
    }
}
