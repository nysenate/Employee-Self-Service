package gov.nysenate.ess.travel.request.model;

import gov.nysenate.ess.core.model.personnel.Employee;

import java.time.LocalDateTime;

public class TravelRequest {

    private int id;
    private Employee travelingEmployee;
    private ModeOfTransportation modeOfTransportation;
    private GsaReimbursement gsaReimbursement;
    private TransportationReimbursement transportationReimbursement;
    private Itinerary itinerary;
    private TravelRequestStatus status;

    private Employee createdBy;
    private LocalDateTime createdDateTime;
    private Employee modifiedBy;
    private LocalDateTime modifiedDateTime;

    private TravelRequest(Builder builder) {
        this.id = builder.id;
        this.travelingEmployee = builder.travelingEmployee;
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

    public Employee getTravelingEmployee() {
        return travelingEmployee;
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

    public TravelRequestStatus getStatus() {
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
        private Employee travelingEmployee;
        private ModeOfTransportation modeOfTransportation;
        private GsaReimbursement gsaReimbursement;
        private TransportationReimbursement transportationReimbursement;
        private Itinerary itinerary;
        private TravelRequestStatus status;
        private Employee createdBy;
        private LocalDateTime createdDateTime;
        private Employee modifiedBy;
        private LocalDateTime modifiedDateTime;

        public TravelRequest build() {
            return new TravelRequest(this);
        }

        public Builder setId(int id) {
            this.id = id;
            return this;
        }

        public Builder setTravelingEmployee(Employee travelingEmployee) {
            this.travelingEmployee = travelingEmployee;
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

        public Builder setStatus(TravelRequestStatus status) {
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
