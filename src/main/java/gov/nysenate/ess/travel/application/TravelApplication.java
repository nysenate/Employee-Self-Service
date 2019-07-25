package gov.nysenate.ess.travel.application;

import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.travel.application.allowances.Allowances;
import gov.nysenate.ess.travel.application.overrides.perdiem.PerDiemOverrides;
import gov.nysenate.ess.travel.application.route.Route;
import gov.nysenate.ess.travel.utils.Dollars;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

public class TravelApplication {

    private final static Comparator<Amendment> amendmentComparator = Comparator.comparingInt(Amendment::id);

    protected int appId;
    protected Employee traveler;
    protected SortedSet<Amendment> amendments;

    public TravelApplication(int id, Employee traveler, Amendment amd) {
        this.appId = id;
        this.traveler = traveler;
        this.amendments = new TreeSet<>(amendmentComparator);
        this.amendments.add(amd);
    }

    public TravelApplication(int appId, int amendmentId, Employee traveler) {
        this.appId = Objects.requireNonNull(appId, "Travel Application requires a non null id.");
        this.traveler = Objects.requireNonNull(traveler, "Travel Application requires a non null traveler.");
        Objects.requireNonNull(amendmentId, "Travel Application requires a non null versionId.");
        this.amendments = new TreeSet<>(amendmentComparator);

        // TODO Some of this is likely wrong
        Amendment a = new Amendment(amendmentId, "", Route.EMPTY_ROUTE, new Allowances(), new PerDiemOverrides(),
                new TravelApplicationStatus(), new ArrayList<>(), LocalDateTime.now(), traveler);
        this.amendments.add(a);
    }

    // The active amendment is the most recent approved amendment
    // or, if none are approved, the most recent amendment.
    public Amendment activeAmendment() {
        Optional<Amendment> approvedAmd = amendments.stream()
                .filter(a -> a.status.isApproved())
                .reduce((first, second) -> second);
        return approvedAmd.orElse(amendments.last());
    }

    public void addAmendment(Amendment a) {
        amendments.add(a);
    }

    public Dollars mileageAllowance() {
        return activeAmendment().perDiemOverrides.isMileageOverridden()
                ? activeAmendment().perDiemOverrides.mileageOverride()
                : getRoute().mileagePerDiems().requestedPerDiem();
    }

    public Dollars mealAllowance() {
        return activeAmendment().perDiemOverrides.isMealsOverridden()
                ? activeAmendment().perDiemOverrides.mealsOverride()
                : getRoute().mealPerDiems().requestedPerDiem();
    }

    public Dollars lodgingAllowance() {
        return activeAmendment().perDiemOverrides.isLodgingOverridden()
                ? activeAmendment().perDiemOverrides.lodgingOverride()
                : getRoute().lodgingPerDiems().requestedPerDiem();
    }

    public Dollars tollsAllowance() {
        return getAllowances().tolls();
    }

    public Dollars parkingAllowance() {
        return getAllowances().parking();
    }

    public Dollars trainAndPlaneAllowance() {
        return getAllowances().trainAndPlane();
    }

    public Dollars alternateTransportationAllowance() {
        return getAllowances().trainAndPlane();
    }

    public Dollars registrationAllowance() {
        return getAllowances().registration();
    }

    /**
     * Total transportation allowance.
     * Used as a field on the print form.
     */
    public Dollars transportationAllowance() {
        return mileageAllowance().add(trainAndPlaneAllowance());
    }

    /**
     * Sum of tolls and parking allowances.
     * Used as a field on the print form.
     */
    public Dollars tollsAndParkingAllowance() {
        return tollsAllowance().add(parkingAllowance());
    }

    /**
     * Total allowance for this travel application.
     */
    public Dollars totalAllowance() {
        return mileageAllowance()
                .add(mealAllowance())
                .add(lodgingAllowance())
                .add(tollsAllowance())
                .add(parkingAllowance())
                .add(trainAndPlaneAllowance())
                .add(alternateTransportationAllowance())
                .add(registrationAllowance());
    }

    /**
     * @return The travel start date or {@code null} if no destinations.
     */
    public LocalDate startDate() {
        return getRoute().startDate();
    }

    /**
     * @return The travel end date or {@code null} if no destinations.
     */
    public LocalDate endDate() {
        return getRoute().endDate();
    }

    public TravelApplicationStatus status() {
        return activeAmendment().status;
    }

    void setStatus(TravelApplicationStatus status) {
        activeAmendment().status = status;
    }

    public void approve() {
        activeAmendment().status.approve();
    }

    public void disapprove(String notes) {
        activeAmendment().status.disapprove(notes);
    }

    public int getAppId() {
        return appId;
    }

    void setAppId(int appId) {
        this.appId = appId;
    }

    public int getVersionId() {
        return activeAmendment().id;
    }

    void setVersionId(int versionId) {
        activeAmendment().id = versionId;
    }

    public Employee getTraveler() {
        return traveler;
    }

    public String getPurposeOfTravel() {
        return activeAmendment().purposeOfTravel;
    }

    void setPurposeOfTravel(String purposeOfTravel) {
        activeAmendment().purposeOfTravel = purposeOfTravel;
    }

    public Route getRoute() {
        return activeAmendment().route;
    }

    void setRoute(Route route) {
        activeAmendment().route = route;
    }

    public Allowances getAllowances() {
        return activeAmendment().allowances;
    }

    void setAllowances(Allowances allowances) {
        activeAmendment().allowances = allowances;
    }

    public PerDiemOverrides getPerDiemOverrides() {
        return activeAmendment().perDiemOverrides;
    }

    void setPerDiemOverrides(PerDiemOverrides perDiemOverrides) {
        activeAmendment().perDiemOverrides = perDiemOverrides;
    }

    public LocalDateTime getSubmittedDateTime() {
        return amendments.first().createdDateTime;
    }

    void setSubmittedDateTime(LocalDateTime submittedDateTime) {
        // TODO Remove/fix this
        amendments.first().createdDateTime = submittedDateTime;
    }

    public LocalDateTime getModifiedDateTime() {
        return amendments.last().createdDateTime;
    }

    void setModifiedDateTime(LocalDateTime modifiedDateTime) {
        // TODO Fix
        amendments.last().createdDateTime = modifiedDateTime;
    }

    public Employee getModifiedBy() {
        return amendments.last().createdBy;
    }

    void setModifiedBy(Employee modifiedBy) {
        // TODO Fix
        amendments.last().createdBy = modifiedBy;
    }

    public List<TravelAttachment> getAttachments() {
        return activeAmendment().attachments;
    }

    void setAttachments(List<TravelAttachment> attachments) {
        // TODO Fix
        activeAmendment().attachments = attachments;
    }

    void addAttachments(List<TravelAttachment> attachments) {
        getAttachments().addAll(attachments);
    }

    void deleteAttachment(String attachmentId) {
        getAttachments().removeIf(a -> a.getId().equals(attachmentId));
    }
}
