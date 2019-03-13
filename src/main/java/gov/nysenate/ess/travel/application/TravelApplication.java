package gov.nysenate.ess.travel.application;

import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.travel.application.allowances.Allowances;
import gov.nysenate.ess.travel.application.route.Route;

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
    private List<TravelAttachment> attachments;
    private LocalDateTime submittedDateTime; // DateTime application was submitted for approval.
    private LocalDateTime modifiedDateTime; // DateTime this app was last updated.
    private Employee modifiedBy; // The last employee to modify.

    public TravelApplication(int appId, int versionId, Employee traveler) {
        this.appId = Objects.requireNonNull(appId, "Travel Application requires a non null id.");
        this.versionId = Objects.requireNonNull(versionId, "Travel Application requires a non null versionId.");
        this.traveler = Objects.requireNonNull(traveler, "Travel Application requires a non null traveler.");
        this.purposeOfTravel = "";
        this.route = Route.EMPTY_ROUTE;
        this.allowances = new Allowances();
        this.attachments = new ArrayList<>();
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

    void setPurposeOfTravel(String purposeOfTravel) {
        this.purposeOfTravel = purposeOfTravel;
    }

    public Route getRoute() {
        return route;
    }

    void setRoute(Route route) {
        this.route = route;
    }

    public Allowances getAllowances() {
        return this.allowances;
    }

    void setAllowances(Allowances allowances) {
        this.allowances = allowances;
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
