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
    private GsaAllowanceView gsaAllowance;
    private TransportationAllowanceView transportationAllowance;
    private String totalAllowance;
    private ItineraryView itinerary;
    private String status;
    private String travelDate;

    private EmployeeView createdBy;
    private String createdDateTime;
    private EmployeeView modifiedBy;
    private String modifiedDateTime;

    public TravelApplicationView(TravelApplication ta) {
        // TODO: Prob need null checks here
        this.id = ta.getId();
        this.applicant = new EmployeeView(ta.getApplicant());
        this.gsaAllowance = new GsaAllowanceView(ta.getGsaAllowance());
        this.transportationAllowance = new TransportationAllowanceView(ta.getTransportationAllowance());
        this.totalAllowance = ta.totalAllowance().toString();
        this.itinerary = new ItineraryView(ta.getItinerary());
        this.status = ta.getStatus().name();
        this.travelDate = ta.travelStartDate().format(DateTimeFormatter.ISO_DATE);
        this.createdBy = new EmployeeView(ta.getCreatedBy());
        this.createdDateTime = ta.getCreatedDateTime().format(DateTimeFormatter.ISO_DATE_TIME);
        this.modifiedBy = new EmployeeView(ta.getModifiedBy());
        this.modifiedDateTime = ta.getModifiedDateTime().format(DateTimeFormatter.ISO_DATE_TIME);
    }

    public TravelApplication toTravelApplication() {
        return TravelApplication.Builder()
                .setId(id)
                .setApplicant(applicant.toEmployee())
                .setGsaAllowance(gsaAllowance.toGsaAllowance())
                .setTransportationAllowance(transportationAllowance.toTransportationAllowance())
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

    public GsaAllowanceView getGsaAllowance() {
        return gsaAllowance;
    }

    public TransportationAllowanceView getTransportationAllowance() {
        return transportationAllowance;
    }

    public String getTotalAllowance() {
        return totalAllowance;
    }

    public ItineraryView getItinerary() {
        return itinerary;
    }

    public String getStatus() {
        return status;
    }

    public String getTravelDate() {
        return travelDate;
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
