package gov.nysenate.ess.travel.application.view;

import gov.nysenate.ess.core.client.view.EmployeeView;
import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.travel.application.model.ModeOfTransportation;
import gov.nysenate.ess.travel.application.model.TravelApplication;
import gov.nysenate.ess.travel.application.model.TravelApplicationStatus;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TravelApplicationView implements ViewObject {

    private int id;
    private EmployeeView applicant;
    private String modeOfTransportation;
    private GsaReimbursementView gsaReimbursement;
    private TransportationReimbursementView transportationReimbursement;
    private ItineraryView itinerary;
    private String status;

    private EmployeeView createdBy;
    private String createdDateTime;
    private EmployeeView modifiedBy;
    private String modifiedDateTime;

    public TravelApplicationView(TravelApplication ta) {
        this.id = ta.getId();
        this.applicant = new EmployeeView(ta.getApplicant());
        this.modeOfTransportation = ta.getModeOfTransportation().name();
        this.gsaReimbursement = new GsaReimbursementView(ta.getGsaReimbursement());
        this.transportationReimbursement = new TransportationReimbursementView(ta.getTransportationReimbursement());
        this.itinerary = new ItineraryView(ta.getItinerary());
        this.status = ta.getStatus().name();
        this.createdBy = new EmployeeView(ta.getCreatedBy());
        this.createdDateTime = ta.getCreatedDateTime().format(DateTimeFormatter.ISO_DATE_TIME);
        this.modifiedBy = new EmployeeView(ta.getModifiedBy());
        this.modifiedDateTime = ta.getModifiedDateTime().format(DateTimeFormatter.ISO_DATE_TIME);
    }

    public TravelApplication toTravelApplication() {
        return TravelApplication.Builder()
                .setId(id)
                .setApplicant(applicant.toEmployee())
                .setModeOfTransportation(ModeOfTransportation.valueOf(modeOfTransportation))
                .setGsaReimbursement(gsaReimbursement.toGsaReimbursement())
                .setTransportationReimbursement(transportationReimbursement.toTransportReimbursement())
                .setItinerary(itinerary.toItinerary())
                .setStatus(TravelApplicationStatus.valueOf(status))
                .setCreatedBy(createdBy.toEmployee())
                .setCreatedDateTime(LocalDateTime.parse(createdDateTime, DateTimeFormatter.ISO_DATE_TIME))
                .setModifiedBy(modifiedBy.toEmployee())
                .setModifiedDateTime(LocalDateTime.parse(modifiedDateTime, DateTimeFormatter.ISO_DATE_TIME))
                .build();
    }

    public int getId() {
        return id;
    }

    public EmployeeView getApplicant() {
        return applicant;
    }

    public String getModeOfTransportation() {
        return modeOfTransportation;
    }

    public GsaReimbursementView getGsaReimbursement() {
        return gsaReimbursement;
    }

    public TransportationReimbursementView getTransportationReimbursement() {
        return transportationReimbursement;
    }

    public ItineraryView getItinerary() {
        return itinerary;
    }

    public String getStatus() {
        return status;
    }

    public EmployeeView getCreatedBy() {
        return createdBy;
    }

    public String getCreatedDateTime() {
        return createdDateTime;
    }

    public EmployeeView getModifiedBy() {
        return modifiedBy;
    }

    public String getModifiedDateTime() {
        return modifiedDateTime;
    }

    @Override
    public String getViewType() {
        return "travel-application";
    }
}
