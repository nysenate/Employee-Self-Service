package gov.nysenate.ess.travel.request.app;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.travel.request.allowances.Allowances;
import gov.nysenate.ess.travel.request.allowances.lodging.LodgingPerDiems;
import gov.nysenate.ess.travel.request.allowances.meal.MealPerDiems;
import gov.nysenate.ess.travel.request.allowances.mileage.MileagePerDiems;
import gov.nysenate.ess.travel.request.amendment.Amendment;
import gov.nysenate.ess.travel.request.attachment.Attachment;
import gov.nysenate.ess.travel.request.route.Route;
import gov.nysenate.ess.travel.utils.Dollars;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

public class TravelApplication {
    private static final Comparator<Amendment> amendmentComparator = Comparator.comparing(Amendment::amendmentId);

    protected int appId;
    protected Employee traveler;
    protected int travelerDeptHeadEmpId;

    private int amendmentId; // 0 if this amendment has not been saved to the database.
    private PurposeOfTravel purposeOfTravel;
    private Route route;
    private Allowances allowances;
    private List<Attachment> attachments;
    private LocalDateTime createdDateTime;
    private Employee createdBy;
    private MealPerDiems mealPerDiems;
    private LodgingPerDiems lodgingPerDiems;
    private MileagePerDiems mileagePerDiems;

    /**
     * The Review Status of this application.
     * null if this application was created before the review process was implemented.
     */
    private TravelApplicationStatus status;
    protected SortedSet<Amendment> amendments;

    public TravelApplication(Employee traveler, Amendment amendment, int travelerDeptHeadEmpId, AppStatus appStatus) {
        this(0, traveler, travelerDeptHeadEmpId, new TravelApplicationStatus(appStatus), Lists.newArrayList(amendment));
    }

    public TravelApplication(int id, Employee traveler, int travelerDeptHeadEmpId,
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


    public Dollars mileageAllowance() {
        return getMileagePerDiems().totalPerDiemValue();
    }

    public Dollars mealAllowance() {
        return getMealPerDiems().total();
    }

    public Dollars lodgingAllowance() {
        return getLodgingPerDiems().totalPerDiem();
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

    public int getAppId() {
        return appId;
    }

    public Employee getTraveler() {
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

    public int getAmendmentId() {
        return amendmentId;
    }

    public void setAmendmentId(int amendmentId) {
        this.amendmentId = amendmentId;
    }

    public PurposeOfTravel getPurposeOfTravel() {
        return purposeOfTravel;
    }

    public void setPurposeOfTravel(PurposeOfTravel purposeOfTravel) {
        this.purposeOfTravel = purposeOfTravel;
    }

    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    public Allowances getAllowances() {
        return allowances;
    }

    public void setAllowances(Allowances allowances) {
        this.allowances = allowances;
    }

    public List<Attachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<Attachment> attachments) {
        this.attachments = attachments;
    }

    public LocalDateTime getCreatedDateTime() {
        return createdDateTime;
    }

    public void setCreatedDateTime(LocalDateTime createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    public Employee getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Employee createdBy) {
        this.createdBy = createdBy;
    }

    public MealPerDiems getMealPerDiems() {
        return mealPerDiems;
    }

    public void setMealPerDiems(MealPerDiems mealPerDiems) {
        this.mealPerDiems = mealPerDiems;
    }

    public LodgingPerDiems getLodgingPerDiems() {
        return lodgingPerDiems;
    }

    public void setLodgingPerDiems(LodgingPerDiems lodgingPerDiems) {
        this.lodgingPerDiems = lodgingPerDiems;
    }

    public MileagePerDiems getMileagePerDiems() {
        return mileagePerDiems;
    }

    public void setMileagePerDiems(MileagePerDiems mileagePerDiems) {
        this.mileagePerDiems = mileagePerDiems;
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
