package gov.nysenate.ess.travel.request.app;

import com.google.common.base.Preconditions;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.travel.request.allowances.Allowances;
import gov.nysenate.ess.travel.request.allowances.lodging.LodgingPerDiems;
import gov.nysenate.ess.travel.request.allowances.meal.MealPerDiems;
import gov.nysenate.ess.travel.request.allowances.mileage.MileagePerDiems;
import gov.nysenate.ess.travel.request.attachment.Attachment;
import gov.nysenate.ess.travel.request.route.Route;
import gov.nysenate.ess.travel.utils.Dollars;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

public class TravelApplication {
    protected int appId;
    protected Employee traveler;
    protected int travelerDeptHeadEmpId;
    private PurposeOfTravel purposeOfTravel;
    private Route route;
    private Allowances allowances;
    private List<Attachment> attachments;
    private LocalDateTime createdDateTime;
    private Employee createdBy;
    private MealPerDiems mealPerDiems;
    private LodgingPerDiems lodgingPerDiems;
    private MileagePerDiems mileagePerDiems;
    private Employee submittedBy;
    private Employee modifiedBy;
    private LocalDateTime modifiedDateTime;
    /**
     * The Review Status of this application.
     * null if this application was created before the review process was implemented.
     */
    private TravelApplicationStatus status;


    public TravelApplication(Employee traveler) {
        this(traveler, 0, null);
    }

    public TravelApplication(Employee traveler, int travelerDeptHeadEmpId, AppStatus appStatus) {
        this(0, traveler, travelerDeptHeadEmpId, new TravelApplicationStatus(appStatus));
    }

    public TravelApplication(int id, Employee traveler, int travelerDeptHeadEmpId,
                             TravelApplicationStatus status) {
        this.appId = id;
        this.traveler = Preconditions.checkNotNull(traveler, "Travel Application requires a non null traveler.");
        this.travelerDeptHeadEmpId = travelerDeptHeadEmpId;
        this.status = status;
        this.purposeOfTravel = null;
        this.route = Route.EMPTY_ROUTE;
        this.allowances = new Allowances();
        this.mealPerDiems = new MealPerDiems(new HashSet<>());
        this.lodgingPerDiems = new LodgingPerDiems(new HashSet<>());
        this.mileagePerDiems = new MileagePerDiems(new HashSet<>());
        this.status = new TravelApplicationStatus(AppStatus.DRAFT);
        this.attachments = new ArrayList<>();
    }

    public int id() {
        return this.appId;
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

    public void setTravelerDeptHeadEmpId(int travelerDeptHeadEmpId) {
        this.travelerDeptHeadEmpId = travelerDeptHeadEmpId;
    }

    public int getTravelerDeptHeadEmpId() {
        return travelerDeptHeadEmpId;
    }

    public LocalDateTime getSubmittedDateTime() {
        return createdDateTime;
    }

    public void setSubmittedBy(Employee submittedBy) {
        this.submittedBy = submittedBy;
    }

    public Employee getSubmittedBy() {
        return submittedBy;
    }

    public LocalDateTime getModifiedDateTime() {
        return modifiedDateTime;
    }

    public Employee getModifiedBy() {
        return modifiedBy;
    }

    public void setAppId(int id) {
        appId = id;
    }

    public void setStatus(TravelApplicationStatus status) {
        this.status = status;
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

    public void setModifiedBy(Employee modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public void setModifiedDateTime(LocalDateTime modifiedDateTime) {
        this.modifiedDateTime = modifiedDateTime;
    }


}
