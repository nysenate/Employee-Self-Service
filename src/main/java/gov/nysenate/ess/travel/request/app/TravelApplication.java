package gov.nysenate.ess.travel.request.app;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.travel.employee.TravelEmployee;
import gov.nysenate.ess.travel.request.amendment.Amendment;
import gov.nysenate.ess.travel.request.amendment.Version;

import java.time.LocalDateTime;
import java.util.*;

public class TravelApplication {

    /** Sort amendments by the declaration order of {@link Version} */
    private static final Comparator<Amendment> amendmentComparator = Comparator.comparing(Amendment::version);

    protected int appId;
    protected TravelEmployee traveler;
    protected int travelerDeptHeadEmpId;

    /**
     * The Review Status of this application.
     * null if this application was created before the review process was implemented.
     */
    private TravelApplicationStatus status;
    protected SortedSet<Amendment> amendments;

    public TravelApplication(TravelEmployee traveler, Amendment amendment, int travelerDeptHeadEmpId, AppStatus appStatus) {
        this(0, traveler, travelerDeptHeadEmpId, new TravelApplicationStatus(appStatus), Lists.newArrayList(amendment));
    }

    public TravelApplication(int id, TravelEmployee traveler, int travelerDeptHeadEmpId,
                             TravelApplicationStatus status, Collection<Amendment> amendments) {
        Preconditions.checkArgument(!amendments.isEmpty());
        this.appId = id;
        this.traveler = Preconditions.checkNotNull(traveler, "Travel Application requires a non null traveler.");
        this.travelerDeptHeadEmpId = travelerDeptHeadEmpId;
        this.status = status;
        this.amendments = new TreeSet<>(amendmentComparator);
        this.amendments.addAll(amendments);
    }

    public int id() {
        return this.appId;
    }

    public Amendment activeAmendment() {
        return amendments.last();
    }

    public SortedSet<Amendment> amendments() {
        return this.amendments;
    }

    public TravelApplicationStatus status() {
        return status;
    }

    public boolean isApproved() {
        return status().isApproved();
    }

    public int getAppId() {
        return appId;
    }

    public TravelEmployee getTraveler() {
        return traveler;
    }

    public int getTravelerDeptHeadEmpId() {
        return travelerDeptHeadEmpId;
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

    public void setAppId(int id) {
        appId = id;
    }

    protected void addAmendment(Amendment a) {
        amendments.add(a);
    }

    public void setStatus(TravelApplicationStatus status) {
        this.status = status;
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
