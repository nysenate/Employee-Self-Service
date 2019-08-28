package gov.nysenate.ess.travel.application;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import gov.nysenate.ess.core.model.personnel.Employee;

import java.time.LocalDateTime;
import java.util.*;

public class TravelApplication {

    /** Sort amendments by the declaration order of {@link Version} */
    private final static Comparator<Amendment> amendmentComparator = Comparator.comparing(Amendment::version);

    protected int appId;
    protected Employee traveler;
    protected SortedSet<Amendment> amendments;

    public TravelApplication(Employee traveler) {
        this(0, traveler, Lists.newArrayList(new Amendment.Builder().withVersion(Version.A).build()));
    }

    public TravelApplication(int id, Employee traveler, Collection<Amendment> amendments) {
        Preconditions.checkArgument(!amendments.isEmpty());
        this.appId = id;
        this.traveler = Preconditions.checkNotNull(traveler, "Travel Application requires a non null traveler.");
        this.amendments = new TreeSet<>(amendmentComparator);
        this.amendments.addAll(amendments);
    }

    // The active amendment is the most recent approved amendment
    // or, if none are approved, the most recent amendment.
    public Amendment activeAmendment() {
//        Optional<Amendment> approvedAmd = amendments.stream()
//                .filter(a -> a.status.isApproved())
//                .reduce((first, second) -> second);
//        return approvedAmd.orElse(amendments.last());
        return amendments.last(); // FIXME for first implementation, just return most recent amendment.
    }

    protected void addAmendment(Amendment a) {
        amendments.add(a);
    }

    public int getAppId() {
        return appId;
    }

    public Employee getTraveler() {
        return traveler;
    }

    public LocalDateTime getSubmittedDateTime() {
        return amendments.first().createdDateTime();
    }

    public LocalDateTime getModifiedDateTime() {
        return amendments.last().createdDateTime();
    }

    public Employee getModifiedBy() {
        return amendments.last().createdBy();
    }
}
