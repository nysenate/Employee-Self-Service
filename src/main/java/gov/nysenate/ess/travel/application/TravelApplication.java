package gov.nysenate.ess.travel.application;

import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.travel.application.allowances.Allowances;
import gov.nysenate.ess.travel.application.overrides.perdiem.PerDiemOverrides;
import gov.nysenate.ess.travel.application.route.Route;
import gov.nysenate.ess.travel.utils.Dollars;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TravelApplication {

    private int appId;
    private int versionId;
    private Employee traveler;
    private String purposeOfTravel;
    private Route route;
    private Allowances allowances;
    private PerDiemOverrides perDiemOverrides;
    private TravelApplicationStatus status;
    private List<TravelAttachment> attachments;
    private LocalDateTime submittedDateTime; // DateTime application was submitted for approval.
    private LocalDateTime modifiedDateTime; // DateTime this app was last updated.
    private Employee modifiedBy; // The last employee to modify.

    public TravelApplication(Employee traveler) {
        this(0, 0, traveler);
    }

    public TravelApplication(int appId, int versionId, Employee traveler) {
        this.appId = Objects.requireNonNull(appId, "Travel Application requires a non null id.");
        this.versionId = Objects.requireNonNull(versionId, "Travel Application requires a non null versionId.");
        this.traveler = Objects.requireNonNull(traveler, "Travel Application requires a non null traveler.");
        this.purposeOfTravel = "";
        this.route = Route.EMPTY_ROUTE;
        this.allowances = new Allowances();
        this.perDiemOverrides = new PerDiemOverrides();
        this.status = new TravelApplicationStatus();
        this.attachments = new ArrayList<>();
    }

    public Dollars mileageAllowance() {
        return perDiemOverrides.isMileageOverridden()
                ? perDiemOverrides.mileageOverride()
                : getRoute().mileagePerDiems().requestedPerDiem();
    }

    public Dollars mealAllowance() {
        return perDiemOverrides.isMealsOverridden()
                ? perDiemOverrides.mealsOverride()
                : getRoute().mealPerDiems().requestedPerDiem();
    }

    public Dollars lodgingAllowance() {
        return perDiemOverrides.isLodgingOverridden()
                ? perDiemOverrides.lodgingOverride()
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
        return getAllowances().alternateTransportation();
    }

    public Dollars registrationAllowance() {
        return allowances.registration();
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
        return status;
    }

    void setStatus(TravelApplicationStatus status) {
        this.status = status;
    }

    public void approve() {
        status.approve();
    }

    public void disapprove(String notes) {
        status.disapprove(notes);
    }

    public int getAppId() {
        return appId;
    }

    void setAppId(int appId) {
        this.appId = appId;
    }

    public int getVersionId() {
        return versionId;
    }

    void setVersionId(int versionId) {
        this.versionId = versionId;
    }

    public Employee getTraveler() {
        return traveler;
    }

    public String getPurposeOfTravel() {
        return purposeOfTravel;
    }

    public void setPurposeOfTravel(String purposeOfTravel) {
        this.purposeOfTravel = purposeOfTravel;
    }

    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    public Allowances getAllowances() {
        return this.allowances;
    }

    public void setAllowances(Allowances allowances) {
        this.allowances = allowances;
    }

    public PerDiemOverrides getPerDiemOverrides() {
        return perDiemOverrides;
    }

    public void setPerDiemOverrides(PerDiemOverrides perDiemOverrides) {
        this.perDiemOverrides = perDiemOverrides;
    }

    public LocalDateTime getSubmittedDateTime() {
        return submittedDateTime;
    }

    void setSubmittedDateTime(LocalDateTime submittedDateTime) {
        this.submittedDateTime = submittedDateTime;
    }

    public LocalDateTime getModifiedDateTime() {
        return modifiedDateTime;
    }

    void setModifiedDateTime(LocalDateTime modifiedDateTime) {
        this.modifiedDateTime = modifiedDateTime;
    }

    public Employee getModifiedBy() {
        return modifiedBy;
    }

    void setModifiedBy(Employee modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public List<TravelAttachment> getAttachments() {
        return attachments;
    }

    void setAttachments(List<TravelAttachment> attachments) {
        this.attachments = attachments;
    }

    void addAttachments(List<TravelAttachment> attachments) {
        getAttachments().addAll(attachments);
    }

    void deleteAttachment(String attachmentId) {
        getAttachments().removeIf(a -> a.getId().equals(attachmentId));
    }
}
