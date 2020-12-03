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
    /**
     * The Review Status of this application.
     * null if this application was created before the review process was implemented.
     */
    private TravelApplicationStatus status;
    protected SortedSet<Amendment> amendments;

    public TravelApplication(Employee traveler, Amendment amendment) {
        this(0, traveler, new TravelApplicationStatus(), Lists.newArrayList(amendment));
    }

    public TravelApplication(int id, Employee traveler, TravelApplicationStatus status, Collection<Amendment> amendments) {
        Preconditions.checkArgument(!amendments.isEmpty());
        this.appId = id;
        this.traveler = Preconditions.checkNotNull(traveler, "Travel Application requires a non null traveler.");
        this.status = status;
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

    public TravelApplicationStatus status() {
        return status;
    }

    /**
     * Mark the TravelApplication as approved.
     */
    public void approve() {
        status().approve();
    }

    /**
     * Mark the TravelApplication as disapproved
     * @param reason The reason for disapproval.
     */
    public void disapprove(String reason) {
        status().disapprove(reason);
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

    public Employee getSubmittedBy() {
        return amendments.first().createdBy();
    }

    public LocalDateTime getModifiedDateTime() {
        return amendments.last().createdDateTime();
    }

    public Employee getModifiedBy() {
        return amendments.last().createdBy();
    }

    public SortedSet<Amendment> getAmendments() {
        return amendments;
    }

    protected void setAppId(int id) {
        appId = id;
    }

    protected void addAmendment(Amendment a) {
        amendments.add(a);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TravelApplication that = (TravelApplication) o;
        return appId == that.appId &&
                Objects.equals(traveler, that.traveler) &&
                Objects.equals(status, that.status) &&
                Objects.equals(amendments, that.amendments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(appId, traveler, status, amendments);
    }
}
