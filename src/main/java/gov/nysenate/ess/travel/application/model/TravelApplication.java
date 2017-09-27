package gov.nysenate.ess.travel.application.model;

import gov.nysenate.ess.core.model.personnel.Employee;

import java.time.LocalDateTime;

public class TravelApplication {

    private int id;
    private Employee applicant;
    private ModeOfTransportation modeOfTransportation;
    private GsaReimbursement gsaReimbursement;
    private TransportationReimbursement transportationReimbursement;
    private Itinerary itinerary;
    private TravelApplicationStatus status;

    private Employee createdBy;
    private LocalDateTime createdDateTime;
    private Employee modifiedBy;
    private LocalDateTime modifiedDateTime;

    private TravelApplication(Builder builder) {
        this.id = builder.id;
        this.applicant = builder.applicant;
        this.modeOfTransportation = builder.modeOfTransportation;
        this.gsaReimbursement = builder.gsaReimbursement;
        this.transportationReimbursement = builder.transportationReimbursement;
        this.itinerary = builder.itinerary;
        this.status = builder.status;
        this.createdBy = builder.createdBy;
        this.createdDateTime = builder.createdDateTime;
        this.modifiedBy = builder.modifiedBy;
        this.modifiedDateTime = builder.modifiedDateTime;
    }

    public int getId() {
        return id;
    }

    public Employee getApplicant() {
        return applicant;
    }

    public ModeOfTransportation getModeOfTransportation() {
        return modeOfTransportation;
    }

    public GsaReimbursement getGsaReimbursement() {
        return gsaReimbursement;
    }

    public TransportationReimbursement getTransportationReimbursement() {
        return transportationReimbursement;
    }

    public Itinerary getItinerary() {
        return itinerary;
    }

    public TravelApplicationStatus getStatus() {
        return status;
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
        private ModeOfTransportation modeOfTransportation;
        private GsaReimbursement gsaReimbursement;
        private TransportationReimbursement transportationReimbursement;
        private Itinerary itinerary;
        private TravelApplicationStatus status;
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

        public Builder setModeOfTransportation(ModeOfTransportation modeOfTransportation) {
            this.modeOfTransportation = modeOfTransportation;
            return this;
        }

        public Builder setGsaReimbursement(GsaReimbursement gsaReimbursement) {
            this.gsaReimbursement = gsaReimbursement;
            return this;
        }

        public Builder setTransportationReimbursement(TransportationReimbursement transportationReimbursement) {
            this.transportationReimbursement = transportationReimbursement;
            return this;
        }

        public Builder setItinerary(Itinerary itinerary) {
            this.itinerary = itinerary;
            return this;
        }

        public Builder setStatus(TravelApplicationStatus status) {
            this.status = status;
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
