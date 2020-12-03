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
import java.util.Objects;

/**
 * A set of changes to a Application.
 * A TravelApplication's first Amendment is created when the application is submitted.
 * From then on, any edit to the application will be done by adding a new Amendment.
 */
public class Amendment {

    private int amendmentId; // 0 if this amendment has not been saved to the database.
    private final Version version;
    private final PurposeOfTravel purposeOfTravel;
    private final Route route;
    private final Allowances allowances;
    private final List<Attachment> attachments;
    private final LocalDateTime createdDateTime;
    private final Employee createdBy;
    private final MealPerDiems mealPerDiems;
    private final LodgingPerDiems lodgingPerDiems;

    public Amendment(Builder builder) {
        amendmentId = builder.amendmentId;
        version = builder.version;
        purposeOfTravel = builder.purposeOfTravel;
        route = builder.route;
        allowances = builder.allowances;
        mealPerDiems = builder.mealPerDiems;
        lodgingPerDiems = builder.lodgingPerDiems;
        attachments = builder.attachments;
        createdDateTime = builder.createdDateTime;
        createdBy = builder.createdBy;
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

    public Route route() {
        return route;
    }

    public MealPerDiems mealPerDiems() {
        return this.mealPerDiems;
    }

    public LodgingPerDiems lodgingPerDiems() {
        return this.lodgingPerDiems;
    }

    protected Allowances allowances() {
        return allowances;
    }

    public List<Attachment> attachments() {
        return attachments;
    }

    public LocalDateTime createdDateTime() {
        return createdDateTime;
    }

    public Employee createdBy() {
        return createdBy;
    }

    protected void setAmendmentId(Integer amendmentId) {
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
        private List<Attachment> attachments = new ArrayList<>();
        private LocalDateTime createdDateTime;
        private Employee createdBy;

        public Builder() {
        }

        /**
         * Initializes a builder as a copy of an existing amendment.
         * {@code createdDateTime} and {@code createdBy} are not copied to the builder.
         * @param amd
         */
        public Builder(Amendment amd) {
            withAmendmentId(amd.amendmentId());
            withVersion(amd.version());
            withPurposeOfTravel(amd.purposeOfTravel());
            withRoute(amd.route());
            withAllowances(amd.allowances());
            withMealPerDiems(amd.mealPerDiems());
            withLodgingPerDiems(amd.lodgingPerDiems());
            withAttachments(amd.attachments());
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

        public Builder withAttachments(List<Attachment> attachments) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Amendment amendment = (Amendment) o;
        return amendmentId == amendment.amendmentId &&
                version == amendment.version &&
                Objects.equals(purposeOfTravel, amendment.purposeOfTravel) &&
                Objects.equals(route, amendment.route) &&
                Objects.equals(allowances, amendment.allowances) &&
                Objects.equals(attachments, amendment.attachments) &&
                Objects.equals(createdDateTime, amendment.createdDateTime) &&
                Objects.equals(createdBy, amendment.createdBy) &&
                Objects.equals(mealPerDiems, amendment.mealPerDiems) &&
                Objects.equals(lodgingPerDiems, amendment.lodgingPerDiems);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amendmentId, version, purposeOfTravel, route, allowances, attachments, createdDateTime, createdBy, mealPerDiems, lodgingPerDiems);
    }
}
