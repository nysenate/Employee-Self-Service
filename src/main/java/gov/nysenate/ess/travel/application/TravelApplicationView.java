package gov.nysenate.ess.travel.application;

import com.fasterxml.jackson.annotation.JsonProperty;
import gov.nysenate.ess.core.client.view.DetailedEmployeeView;
import gov.nysenate.ess.core.client.view.EmployeeView;
import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.travel.application.allowances.mileage.MileageAllowancesView;
import gov.nysenate.ess.travel.application.allowances.lodging.LodgingAllowancesView;
import gov.nysenate.ess.travel.application.allowances.meal.MealAllowancesView;
import gov.nysenate.ess.travel.application.destination.DestinationsView;
import gov.nysenate.ess.travel.application.route.RouteView;
import gov.nysenate.ess.travel.utils.Dollars;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.time.format.DateTimeFormatter.*;

public class TravelApplicationView implements ViewObject {

    String id;
    String versionId;
    DetailedEmployeeView traveler;
    DetailedEmployeeView submitter;
    String purposeOfTravel;
    RouteView route;
    DestinationsView accommodations;
    MileageAllowancesView mileageAllowance;
    MealAllowancesView mealAllowance;
    LodgingAllowancesView lodgingAllowance;
    String tollsAllowance = "0";
    String parkingAllowance = "0";
    String alternateAllowance = "0";
    String trainAndAirplaneAllowance = "0";
    String registrationAllowance = "0";
    String totalAllowance = "0";
    String transportationAllowance;
    String startDate;
    String endDate;
    String submittedDateTime;
    String modifiedDateTime;
    DetailedEmployeeView modifiedBy;
    List<TravelAttachmentView> attachments;
    @JsonProperty(value="isDeleted")
    boolean isDeleted;

    public TravelApplicationView() {
    }

    public TravelApplicationView(TravelApplication app) {
        id = app.getId().toString();
        versionId = app.getVersionId().toString();
        traveler = new DetailedEmployeeView(app.getTraveler());
        submitter = new DetailedEmployeeView(app.getSubmitter());
        purposeOfTravel = app.getPurposeOfTravel();
        route = new RouteView(app.getRoute());
        accommodations = new DestinationsView(app.getDestinations());
        mileageAllowance = new MileageAllowancesView(app.getMileageAllowances());
        mealAllowance = new MealAllowancesView(app.getMealAllowances());
        lodgingAllowance = new LodgingAllowancesView(app.getLodgingAllowances());
        tollsAllowance = app.getTolls().toString();
        parkingAllowance = app.getParking().toString();
        alternateAllowance = app.getAlternate().toString();
        trainAndAirplaneAllowance = app.getTrainAndAirplane().toString();
        registrationAllowance = app.getRegistration().toString();
        totalAllowance = app.totalAllowance().toString();
        transportationAllowance = app.transportationAllowance().toString();
        submittedDateTime = app.getSubmittedDateTime() == null ? null : app.getSubmittedDateTime().format(ISO_DATE_TIME);
        modifiedDateTime = app.getModifiedDateTime() == null ? null : app.getModifiedDateTime().format(ISO_DATE_TIME);
        modifiedBy = app.getModifiedBy() == null ? null : new DetailedEmployeeView(app.getModifiedBy());
        startDate = app.startDate() == null ? "" : app.startDate().format(ISO_DATE);
        endDate = app.endDate() == null ? "" : app.endDate().format(ISO_DATE);
        attachments = app.getAttachments().stream().map(TravelAttachmentView::new).collect(Collectors.toList());
        isDeleted = app.isDeleted();
    }

    public TravelApplication toTravelApplication() {
        TravelApplication app = new TravelApplication(UUID.fromString(id), UUID.fromString(versionId),
                traveler.toEmployee(), submitter.toEmployee());
        if (accommodations != null) {
            app.setDestinations(this.accommodations.toDestinations());
        }
        if (route != null) {
            app.setRoute(route.toRoute());
        }
        app.setPurposeOfTravel(purposeOfTravel);
        app.setMileageAllowances(mileageAllowance.toMileageAllowances());
        app.setMealAllowances(mealAllowance.toMealAllowances());
        app.setLodgingAllowances(lodgingAllowance.toLodgingAllowances());
        app.setTolls(new Dollars(tollsAllowance));
        app.setParking(new Dollars(parkingAllowance));
        app.setAlternate(new Dollars(alternateAllowance));
        app.setTrainAndAirplane(new Dollars(trainAndAirplaneAllowance));
        app.setRegistration(new Dollars(registrationAllowance));
        app.setSubmittedDateTime(submittedDateTime == null ? null : LocalDateTime.parse(submittedDateTime, ISO_DATE_TIME));
        app.setModifiedDateTime(modifiedDateTime == null ? null : LocalDateTime.parse(modifiedDateTime, ISO_DATE_TIME));
        app.setModifiedBy(modifiedBy == null ? null : modifiedBy.toEmployee());
        app.setAttachments(attachments.stream().map(TravelAttachmentView::toTravelAttachment).collect(Collectors.toList()));
        app.setDeleted(isDeleted);
        return app;
    }

    public String getId() {
        return id;
    }

    public String getVersionId() {
        return versionId;
    }

    public EmployeeView getTraveler() {
        return traveler;
    }

    public void setTraveler(DetailedEmployeeView traveler) {
        this.traveler = traveler;
    }

    public EmployeeView getSubmitter() {
        return submitter;
    }

    public void setSubmitter(DetailedEmployeeView submitter) {
        this.submitter = submitter;
    }

    public DestinationsView getAccommodations() {
        return accommodations;
    }

    public void setAccommodations(DestinationsView accommodations) {
        this.accommodations = accommodations;
    }

    public RouteView getRoute() {
        return route;
    }

    public void setRoute(RouteView route) {
        this.route = route;
    }

    public String getPurposeOfTravel() {
        return purposeOfTravel;
    }

    public void setPurposeOfTravel(String purposeOfTravel) {
        this.purposeOfTravel = purposeOfTravel;
    }

    public MileageAllowancesView getMileageAllowance() {
        return mileageAllowance;
    }

    public void setMileageAllowance(MileageAllowancesView mileageAllowance) {
        this.mileageAllowance = mileageAllowance;
    }

    public MealAllowancesView getMealAllowance() {
        return mealAllowance;
    }

    public void setMealAllowance(MealAllowancesView mealAllowance) {
        this.mealAllowance = mealAllowance;
    }

    public LodgingAllowancesView getLodgingAllowance() {
        return lodgingAllowance;
    }

    public void setLodgingAllowance(LodgingAllowancesView lodgingAllowance) {
        this.lodgingAllowance = lodgingAllowance;
    }

    public void setAttachments(List<TravelAttachmentView> attachments) {
        this.attachments = attachments;
    }

    public String getTollsAllowance() {
        return tollsAllowance;
    }

    public void setTollsAllowance(String tollsAllowance) {
        this.tollsAllowance = tollsAllowance;
    }

    public String getParkingAllowance() {
        return parkingAllowance;
    }

    public void setParkingAllowance(String parkingAllowance) {
        this.parkingAllowance = parkingAllowance;
    }

    public String getAlternateAllowance() {
        return alternateAllowance;
    }

    public void setAlternateAllowance(String alternateAllowance) {
        this.alternateAllowance = alternateAllowance;
    }

    public String getTrainAndAirplaneAllowance() {
        return trainAndAirplaneAllowance;
    }

    public void setTrainAndAirplaneAllowance(String trainAndAirplaneAllowance) {
        this.trainAndAirplaneAllowance = trainAndAirplaneAllowance;
    }

    public String getTransportationAllowance() {
        return transportationAllowance;
    }

    public void setTransportationAllowance(String transportationAllowance) {
        this.transportationAllowance = transportationAllowance;
    }

    public String getRegistrationAllowance() {
        return registrationAllowance;
    }

    public void setRegistrationAllowance(String registrationAllowance) {
        this.registrationAllowance = registrationAllowance;
    }

    public String getTotalAllowance() {
        return totalAllowance;
    }

    public void setTotalAllowance(String totalAllowance) {
        this.totalAllowance = totalAllowance;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getSubmittedDateTime() {
        return submittedDateTime;
    }

    public void setSubmittedDateTime(String submittedDateTime) {
        this.submittedDateTime = submittedDateTime;
    }

    public String getModifiedDateTime() {
        return modifiedDateTime;
    }

    public void setModifiedDateTime(String modifiedDateTime) {
        this.modifiedDateTime = modifiedDateTime;
    }

    public DetailedEmployeeView getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(DetailedEmployeeView modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public List<TravelAttachmentView> getAttachments() {
        return attachments;
    }

    @JsonProperty(value="isDeleted")
    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    @Override
    public String getViewType() {
        return "travel-application";
    }
}
