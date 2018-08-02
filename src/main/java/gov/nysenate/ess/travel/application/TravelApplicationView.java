package gov.nysenate.ess.travel.application;

import gov.nysenate.ess.core.client.view.DetailedEmployeeView;
import gov.nysenate.ess.core.client.view.EmployeeView;
import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.travel.MileageAllowancesView;
import gov.nysenate.ess.travel.application.allowances.lodging.LodgingAllowancesView;
import gov.nysenate.ess.travel.application.allowances.meal.MealAllowancesView;
import gov.nysenate.ess.travel.application.destination.DestinationView;
import gov.nysenate.ess.travel.application.destination.Destinations;
import gov.nysenate.ess.travel.application.destination.DestinationsView;
import gov.nysenate.ess.travel.route.RouteView;
import gov.nysenate.ess.travel.utils.Dollars;

import java.util.List;
import java.util.stream.Collectors;

import static java.time.format.DateTimeFormatter.*;

public class TravelApplicationView implements ViewObject {

    long id;
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
    String registrationAllowance = "0";
    String totalAllowance = "0";
    String startDate;
    String endDate;
    String submittedDateTime;
    List<TravelAttachmentView> attachments;

    public TravelApplicationView() {
    }

    public TravelApplicationView(TravelApplication app) {
        id = app.getId();
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
        registrationAllowance = app.getRegistration().toString();
        totalAllowance = app.totalAllowance().toString();
        submittedDateTime = app.getSubmittedDateTime() == null ? null : app.getSubmittedDateTime().format(ISO_DATE_TIME);
        startDate = app.startDate() == null ? "" : app.startDate().format(ISO_DATE);
        endDate = app.endDate() == null ? "" : app.endDate().format(ISO_DATE);
        attachments = app.getAttachments().stream().map(TravelAttachmentView::new).collect(Collectors.toList());
    }

    public TravelApplication toTravelApplication() {
        TravelApplication app = new TravelApplication(id, traveler.toEmployee(), submitter.toEmployee());
        if (accommodations != null) {
            app.setDestinations(this.accommodations.toDestinations());
        }
        if (route != null) {
            app.setRoute(route.toRoute());
        }
        app.setPurposeOfTravel(purposeOfTravel);
        app.setMileageAllowances(mileageAllowance.toMileageAllowanceView());
        app.setMealAllowances(mealAllowance.toMealAllowances());
        app.setLodgingAllowances(lodgingAllowance.toLodgingAllowances());
        app.setTolls(new Dollars(tollsAllowance));
        app.setParking(new Dollars(parkingAllowance));
        app.setAlternate(new Dollars(alternateAllowance));
        app.setRegistration(new Dollars(registrationAllowance));
        app.setAttachments(attachments.stream().map(TravelAttachmentView::toTravelAttachment).collect(Collectors.toList()));
        return app;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public List<TravelAttachmentView> getAttachments() {
        return attachments;
    }

    @Override
    public String getViewType() {
        return "travel-application";
    }
}
