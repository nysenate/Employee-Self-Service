package gov.nysenate.ess.travel.application;

import gov.nysenate.ess.core.client.view.EmployeeView;
import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.travel.accommodation.Accommodation;
import gov.nysenate.ess.travel.accommodation.AccommodationView;
import gov.nysenate.ess.travel.route.RouteView;
import gov.nysenate.ess.travel.utils.Dollars;

import java.util.List;
import java.util.stream.Collectors;

import static java.time.format.DateTimeFormatter.*;

public class TravelApplicationView implements ViewObject {

    private long id;
    private EmployeeView traveler;
    private EmployeeView submitter;
    private List<AccommodationView> accommodations;
    private RouteView route;
    private String purposeOfTravel;
    private String mileageAllowance = "0";
    private String mealAllowance = "0";
    private String lodgingAllowance = "0";
    private String tollsAllowance = "0";
    private String parkingAllowance = "0";
    private String alternateAllowance = "0";
    private String registrationAllowance = "0";
    private String totalAllowance = "0";
    private String startDate;
    private String endDate;
    private String submittedDateTime;
    private List<TravelAttachmentView> attachments;

    public TravelApplicationView() {
    }

    public TravelApplicationView(TravelApplication app) {
        id = app.getId();
        traveler = new EmployeeView(app.getTraveler());
        submitter = new EmployeeView(app.getSubmitter());
        accommodations = app.getAccommodations().stream()
                .map(AccommodationView::new)
                .collect(Collectors.toList());
        route = new RouteView(app.getRoute());
        purposeOfTravel = app.getPurposeOfTravel();
        mileageAllowance = app.mileageAllowance().toString();
        mealAllowance = app.mealAllowance().toString();
        lodgingAllowance = app.lodgingAllowance().toString();
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
            app.setAccommodations(accommodations.stream().map(AccommodationView::toAccommodation).collect(Collectors.toList()));
        }
        if (route != null) {
            app.setRoute(route.toRoute());
        }
        app.setPurposeOfTravel(purposeOfTravel);
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

    public void setTraveler(EmployeeView traveler) {
        this.traveler = traveler;
    }

    public EmployeeView getSubmitter() {
        return submitter;
    }

    public void setSubmitter(EmployeeView submitter) {
        this.submitter = submitter;
    }

    public List<AccommodationView> getAccommodations() {
        return accommodations;
    }

    public void setAccommodations(List<Accommodation> accommodations) {
        this.accommodations = accommodations.stream().map(AccommodationView::new).collect(Collectors.toList());

        Dollars mealAllowance = Dollars.ZERO;
        for (Accommodation a : accommodations) {
            mealAllowance = mealAllowance.add(a.mealAllowance());
        }
        setMealAllowance(mealAllowance.toString());

        Dollars lodgingAllowance = Dollars.ZERO;
        for (Accommodation a : accommodations) {
            lodgingAllowance = lodgingAllowance.add(a.lodgingAllowance());
        }
        setLodgingAllowance(lodgingAllowance.toString());
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

    public String getMileageAllowance() {
        return mileageAllowance;
    }

    public void setMileageAllowance(String mileageAllowance) {
        this.mileageAllowance = mileageAllowance;
    }

    public String getMealAllowance() {
        return mealAllowance;
    }

    public void setMealAllowance(String mealAllowance) {
        this.mealAllowance = mealAllowance;
    }

    public String getLodgingAllowance() {
        return lodgingAllowance;
    }

    public void setLodgingAllowance(String lodgingAllowance) {
        this.lodgingAllowance = lodgingAllowance;
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
