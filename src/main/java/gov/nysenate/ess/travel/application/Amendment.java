package gov.nysenate.ess.travel.application;

import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.travel.application.allowances.Allowances;
import gov.nysenate.ess.travel.application.allowances.lodging.LodgingPerDiems;
import gov.nysenate.ess.travel.application.allowances.meal.MealPerDiems;
import gov.nysenate.ess.travel.application.route.Route;
import gov.nysenate.ess.travel.utils.Dollars;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * A set of changes to a Application.
 */
public class Amendment {

    private int amendmentId; // 0 if this amendment has not been saved to the database.
    private Version version;
    private PurposeOfTravel purposeOfTravel;
    private Route route;
    private Allowances allowances;
    private TravelApplicationStatus status;
    private List<TravelAttachment> attachments;
    private LocalDateTime createdDateTime;
    private Employee createdBy;
    private MealPerDiems mealPerDiems;
    private LodgingPerDiems lodgingPerDiems;

    public Amendment(Builder builder) {
        amendmentId = builder.amendmentId;
        version = builder.version;
        purposeOfTravel = builder.purposeOfTravel;
        route = builder.route;
        allowances = builder.allowances;
        mealPerDiems = builder.mealPerDiems;
        lodgingPerDiems = builder.lodgingPerDiems;
        status = builder.status;
        attachments = builder.attachments;
        createdDateTime = builder.createdDateTime;
        createdBy = builder.createdBy;
    }

    public void approve() {
        status().approve();
    }

    public void disapprove(String reason) {
        status().disapprove(reason);
    }

    public Dollars mileageAllowance() {
        return route().mileagePerDiems().totalPerDiem();
    }

    public Dollars mealAllowance() {
        return mealPerDiems().totalPerDiem();
    }

    public Dollars lodgingAllowance() {
        return lodgingPerDiems().totalPerDiem();
    }

    public Dollars tollsAllowance() {
        return allowances().tolls();
    }

    public Dollars parkingAllowance() {
        return allowances().parking();
    }

    public Dollars trainAndPlaneAllowance() {
        return allowances().trainAndPlane();
    }

    public Dollars alternateTransportationAllowance() {
        return allowances().alternateTransportation();
    }

    public Dollars registrationAllowance() {
        return allowances().registration();
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
        return route().startDate();
    }

    /**
     * @return The travel end date or {@code null} if no destinations.
     */
    public LocalDate endDate() {
        return route().endDate();
    }

    public int amendmentId() {
        return amendmentId;
    }

    public Version version() {
        return version;
    }

    public PurposeOfTravel purposeOfTravel() {
        return purposeOfTravel;
    }

    public void setPurposeOfTravel(PurposeOfTravel purposeOfTravel) {
        this.purposeOfTravel = purposeOfTravel;
    }

    public Route route() {
        return route;
    }

    /**
     * Setting of the route should be done with {@link AmendmentService}, Not this setter.
     * @param route
     */
    public void setRoute(Route route) {
        this.route = route;
    }

    public MealPerDiems mealPerDiems() {
        return this.mealPerDiems;
    }

    public void setMealPerDiems(MealPerDiems mpds) {
        this.mealPerDiems = mpds;
    }

    public LodgingPerDiems lodgingPerDiems() {
        return this.lodgingPerDiems;
    }

    public void setLodingPerDiems(LodgingPerDiems lpds) {
        this.lodgingPerDiems = lpds;
    }

    protected Allowances allowances() {
        return allowances;
    }

    public void setAllowances(Allowances allowances) {
        this.allowances = allowances;
    }

    public TravelApplicationStatus status() {
        return status;
    }

    public List<TravelAttachment> attachments() {
        return attachments;
    }

    public LocalDateTime createdDateTime() {
        return createdDateTime;
    }

    public Employee createdBy() {
        return createdBy;
    }

    public void setCreatedDateTime(LocalDateTime createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    public void setCreatedBy(Employee createdBy) {
        this.createdBy = createdBy;
    }

    public void setAmendmentId(Integer amendmentId) {
        this.amendmentId = amendmentId;
    }


    /**
     * Amendment Builder
     */
    public static class Builder {
        private int amendmentId = 0;
        private Version version = Version.A;
        private PurposeOfTravel purposeOfTravel = null;
        private Route route = Route.EMPTY_ROUTE;
        private Allowances allowances = new Allowances();
        private MealPerDiems mealPerDiems = new MealPerDiems(new HashSet<>());
        private LodgingPerDiems lodgingPerDiems = new LodgingPerDiems(new HashSet<>());
        private TravelApplicationStatus status = new TravelApplicationStatus();
        private List<TravelAttachment> attachments = new ArrayList<>();
        private LocalDateTime createdDateTime;
        private Employee createdBy;

        public Builder() {
        }

        public Builder withAmendmentId(int id) {
            this.amendmentId = id;
            return this;
        }

        public Builder withVersion(Version version) {
            this.version = version;
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

        public Builder withMealPerDiems(MealPerDiems mpds) {
            this.mealPerDiems = mpds;
            return this;
        }

        public Builder withLodgingPerDiems(LodgingPerDiems lpds) {
            this.lodgingPerDiems = lpds;
            return this;
        }

        public Builder withStatus(TravelApplicationStatus status) {
            this.status = status;
            return this;
        }

        public Builder withAttachments(List<TravelAttachment> attachments) {
            this.attachments = attachments;
            return this;
        }

        public Builder withCreatedDateTime(LocalDateTime createdDateTime) {
            this.createdDateTime = createdDateTime;
            return this;
        }

        public Builder withCreatedBy(Employee createdBy) {
            this.createdBy = createdBy;
            return this;
        }

        public Amendment build() {
            return new Amendment(this);
        }
    }
}
