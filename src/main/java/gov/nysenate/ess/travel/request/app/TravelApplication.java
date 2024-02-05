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
    private MealPerDiems mealPerDiems;
    private LodgingPerDiems lodgingPerDiems;
    private MileagePerDiems mileagePerDiems;
    /**
     * The Review Status of this application.
     * null if this application was created before the review process was implemented.
     */
    private TravelApplicationStatus status;
    private Employee createdBy;
    private Employee modifiedBy;
    private LocalDateTime createdDateTime;
    private LocalDateTime modifiedDateTime;

    public TravelApplication(Builder builder) {
        this.appId = builder.appId;
        this.traveler = Preconditions.checkNotNull(builder.traveler, "A Travel Application requires a non null traveler.");
        this.travelerDeptHeadEmpId = builder.travelerDeptHeadEmpId;
        this.purposeOfTravel = builder.purposeOfTravel;
        this.route = builder.route;
        this.allowances = builder.allowances;
        this.attachments = builder.attachments;
        this.mealPerDiems = builder.mealPerDiems;
        this.lodgingPerDiems = builder.lodgingPerDiems;
        this.mileagePerDiems = builder.mileagePerDiems;
        this.status = builder.status;
        this.createdBy = builder.createdBy;
        this.modifiedBy = builder.modifiedBy;
        this.createdDateTime = builder.createdDateTime;
        this.modifiedDateTime = builder.modifiedDateTime;
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

    public void setCreatedBy(Employee createdBy) {
        this.createdBy = createdBy;
    }

    public Employee getCreatedBy() {
        return createdBy;
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

    public static class Builder {
        private int appId = 0;
        private final Employee traveler;
        private final int travelerDeptHeadEmpId;
        private PurposeOfTravel purposeOfTravel;
        private Route route = Route.EMPTY_ROUTE;
        private Allowances allowances = new Allowances();
        private List<Attachment> attachments = new ArrayList<>();
        private MealPerDiems mealPerDiems = new MealPerDiems(new HashSet<>());
        private LodgingPerDiems lodgingPerDiems = new LodgingPerDiems(new HashSet<>());
        private MileagePerDiems mileagePerDiems = new MileagePerDiems(new HashSet<>());
        private TravelApplicationStatus status = new TravelApplicationStatus(AppStatus.DRAFT);
        private Employee createdBy;
        private Employee modifiedBy;
        private LocalDateTime createdDateTime;
        private LocalDateTime modifiedDateTime;

        public Builder(Employee traveler, int travelerDeptHeadEmpId) {
            this.traveler = traveler;
            this.travelerDeptHeadEmpId = travelerDeptHeadEmpId;
        }

        public Builder withAppId(int appId) {
            this.appId = appId;
            return this;
        }

        public Builder withPurposeOfTravel(PurposeOfTravel purposeOfTravel) {
            this.purposeOfTravel = purposeOfTravel;
            return this;
        }

        public Builder withRoute(Route route) {
            this.route = route;
            return this;
        }

        public Builder withAllowances(Allowances allowances) {
            this.allowances = allowances;
            return this;
        }

        public Builder withAttachments(List<Attachment> attachments) {
            this.attachments = attachments;
            return this;
        }

        public Builder withCreatedDateTime(LocalDateTime createdDateTime) {
            this.createdDateTime = createdDateTime;
            return this;
        }

        public Builder withMealPerDiems(MealPerDiems mealPerDiems) {
            this.mealPerDiems = mealPerDiems;
            return this;
        }

        public Builder withLodgingPerDiems(LodgingPerDiems lodgingPerDiems) {
            this.lodgingPerDiems = lodgingPerDiems;
            return this;
        }

        public Builder withMileagePerDiems(MileagePerDiems mileagePerDiems) {
            this.mileagePerDiems = mileagePerDiems;
            return this;
        }

        public Builder withCreatedBy(Employee createdBy) {
            this.createdBy = createdBy;
            return this;
        }

        public Builder withModifiedBy(Employee modifiedBy) {
            this.modifiedBy = modifiedBy;
            return this;
        }

        public Builder withModifiedDateTime(LocalDateTime modifiedDateTime) {
            this.modifiedDateTime = modifiedDateTime;
            return this;
        }

        public Builder withStatus(TravelApplicationStatus status) {
            this.status = status;
            return this;
        }

        public TravelApplication build() {
            return new TravelApplication(this);
        }
    }

}
