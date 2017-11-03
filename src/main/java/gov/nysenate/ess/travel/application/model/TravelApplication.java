package gov.nysenate.ess.travel.application.model;

import gov.nysenate.ess.core.model.personnel.Employee;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public class TravelApplication {

    private int id;
    private Employee applicant;
    private TravelAllowances allowances;
    private Itinerary itinerary;
    private ModeOfTransportation modeOfTransportation;
    private TravelApplicationStatus status;
    private String purposeOfTravel;

    private Employee createdBy;
    private LocalDateTime createdDateTime;
    private Employee modifiedBy;
    private LocalDateTime modifiedDateTime;

    private TravelApplication(Builder builder) {
        checkNotNull(builder.applicant);
        checkNotNull(builder.allowances);
        checkNotNull(builder.modeOfTransportation);
        checkNotNull(builder.itinerary);
        checkNotNull(builder.createdBy);
        checkArgument(!builder.applicant.isEmpty());
        this.id = builder.id;
        this.applicant = builder.applicant;
        this.allowances = builder.allowances;
        this.itinerary = builder.itinerary;
        this.status = builder.status;
        this.purposeOfTravel = builder.purposeOfTravel;
        this.createdBy = builder.createdBy;
        this.createdDateTime = builder.createdDateTime;
        this.modifiedBy = builder.modifiedBy;
        this.modifiedDateTime = builder.modifiedDateTime;
        this.modeOfTransportation = builder.modeOfTransportation;
    }

    /**
     * The total allowance available to the applicant.
     * @return
     */
    public BigDecimal totalAllowance() {
        return getAllowances().total();
    }

    /**
     * The date the applicant will arrive at their first destination.
     */
    public LocalDate travelStartDate() {
        return getItinerary().startDate();
    }

    /**
     * The date the applicant will depart from their final destination.
     */
    public LocalDate travelEndDate() {
        return getItinerary().endDate();
    }

    public int getId() {
        return id;
    }

    public Employee getApplicant() {
        return applicant;
    }

    public TravelAllowances getAllowances() {
        return allowances;
    }

    public Itinerary getItinerary() {
        return itinerary;
    }

    public ModeOfTransportation getModeOfTransportation() {
        return modeOfTransportation;
    }

    public TravelApplicationStatus getStatus() {
        return status;
    }

    public String getPurposeOfTravel() {
        return purposeOfTravel;
    }

    public Employee getCreatedBy() {
        return createdBy;
    }

    public LocalDateTime getCreatedDateTime() {
        return createdDateTime;
    }

    public Employee getModifiedBy() {
        return modifiedBy;
    }

    public LocalDateTime getModifiedDateTime() {
        return modifiedDateTime;
    }

    public static Builder Builder() {
        return new Builder();
    }

    public static class Builder {
        private int id;
        private Employee applicant;
        private TravelAllowances allowances;
        private Itinerary itinerary;
        private ModeOfTransportation modeOfTransportation;
        private TravelApplicationStatus status;
        private String purposeOfTravel;

        private Employee createdBy;
        private LocalDateTime createdDateTime;
        private Employee modifiedBy;
        private LocalDateTime modifiedDateTime;

        public TravelApplication build() {
            return new TravelApplication(this);
        }

        public Builder setId(int id) {
            this.id = id;
            return this;
        }

        public Builder setApplicant(Employee applicant) {
            this.applicant = applicant;
            return this;
        }

        public Builder setAllowances(TravelAllowances allowances) {
            this.allowances = allowances;
            return this;
        }

        public Builder setItinerary(Itinerary itinerary) {
            this.itinerary = itinerary;
            return this;
        }

        public Builder setModeOfTransportation(ModeOfTransportation modeOfTransportation) {
            this.modeOfTransportation = modeOfTransportation;
            return this;
        }

        public Builder setStatus(TravelApplicationStatus status) {
            this.status = status;
            return this;
        }

        public Builder setPurposeOfTravel(String purposeOfTravel) {
            this.purposeOfTravel = purposeOfTravel;
            return this;
        }

        public Builder setCreatedBy(Employee createdBy) {
            this.createdBy = createdBy;
            return this;
        }

        public Builder setCreatedDateTime(LocalDateTime createdDateTime) {
            this.createdDateTime = createdDateTime;
            return this;
        }

        public Builder setModifiedBy(Employee modifiedBy) {
            this.modifiedBy = modifiedBy;
            return this;
        }

        public Builder setModifiedDateTime(LocalDateTime modifiedDateTime) {
            this.modifiedDateTime = modifiedDateTime;
            return this;
        }
    }
}
