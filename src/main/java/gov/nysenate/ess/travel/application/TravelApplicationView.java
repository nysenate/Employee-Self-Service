package gov.nysenate.ess.travel.application;

import gov.nysenate.ess.core.client.view.AddressView;
import gov.nysenate.ess.core.client.view.EmployeeView;
import gov.nysenate.ess.core.client.view.base.ListView;
import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.travel.accommodation.AccommodationView;
import gov.nysenate.ess.travel.route.RouteView;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.time.format.DateTimeFormatter.*;

public class TravelApplicationView implements ViewObject {

    private long id;
    private EmployeeView traveler;
    private EmployeeView submitter;
    private List<AccommodationView> accommodations;
    private RouteView route;
    private AddressView origin;
    private List<TravelDestinationView> destinations;
    private String purposeOfTravel;
    private String mileageAllowance;
    private String mealAllowance;
    private String lodgingAllowance;
    private String tollsAllowance;
    private String parkingAllowance;
    private String alternateAllowance;
    private String registrationAllowance;
    private String totalAllowance;
    private String startDate;
    private String endDate;
    private String submittedDateTime;

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
        destinations = new ArrayList<>();
        for (AccommodationView a : accommodations) {
            destinations.add(new TravelDestinationView(a, route));
        }
        origin = route.getOrigin();
        startDate = app.startDate() == null ? "" : app.startDate().format(ISO_DATE);
        endDate = app.endDate() == null ? "" : app.endDate().format(ISO_DATE);
    }

    public long getId() {
        return id;
    }

    public EmployeeView getTraveler() {
        return traveler;
    }

    public EmployeeView getSubmitter() {
        return submitter;
    }

    public List<AccommodationView> getAccommodations() {
        return accommodations;
    }

    public RouteView getRoute() {
        return route;
    }

    public String getPurposeOfTravel() {
        return purposeOfTravel;
    }

    public String getMileageAllowance() {
        return mileageAllowance;
    }

    public String getMealAllowance() {
        return mealAllowance;
    }

    public String getLodgingAllowance() {
        return lodgingAllowance;
    }

    public String getTollsAllowance() {
        return tollsAllowance;
    }

    public String getParkingAllowance() {
        return parkingAllowance;
    }

    public String getAlternateAllowance() {
        return alternateAllowance;
    }

    public String getRegistrationAllowance() {
        return registrationAllowance;
    }

    public String getTotalAllowance() {
        return totalAllowance;
    }

    public String getSubmittedDateTime() {
        return submittedDateTime;
    }

    public List<TravelDestinationView> getDestinations() {
        return destinations;
    }

    public AddressView getOrigin() {
        return origin;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    @Override
    public String getViewType() {
        return "travel-application";
    }
}
