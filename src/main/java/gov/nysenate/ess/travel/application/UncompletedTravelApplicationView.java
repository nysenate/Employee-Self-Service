package gov.nysenate.ess.travel.application;

import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.travel.application.allowances.lodging.LodgingAllowancesView;
import gov.nysenate.ess.travel.application.allowances.meal.MealAllowancesView;
import gov.nysenate.ess.travel.application.allowances.mileage.MileageAllowancesView;
import gov.nysenate.ess.travel.application.destination.DestinationsView;
import gov.nysenate.ess.travel.application.route.RouteView;


/**
 * Very similar to {@link TravelApplicationView} except this class
 * uses id's for employees instead of employee views due to issues with
 * serialization/deserialization.
 *
 * Also omits some fields not relevant to an uncompleted application, such as submitted date time.
 */
public class UncompletedTravelApplicationView implements ViewObject {

    String id;
    String versionId;
    int travelerId;
    int submitterId;
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

    public UncompletedTravelApplicationView() {
    }

    public UncompletedTravelApplicationView(TravelApplication app) {
        id = app.getId().toString();
        versionId = app.getVersionId().toString();
        travelerId = app.getTraveler().getEmployeeId();
        submitterId = app.getSubmitter().getEmployeeId();
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
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVersionId() {
        return versionId;
    }

    public void setVersionId(String versionId) {
        this.versionId = versionId;
    }

    public int getTravelerId() {
        return travelerId;
    }

    public void setTravelerId(int travelerId) {
        this.travelerId = travelerId;
    }

    public int getSubmitterId() {
        return submitterId;
    }

    public void setSubmitterId(int submitterId) {
        this.submitterId = submitterId;
    }

    public String getPurposeOfTravel() {
        return purposeOfTravel;
    }

    public void setPurposeOfTravel(String purposeOfTravel) {
        this.purposeOfTravel = purposeOfTravel;
    }

    public RouteView getRoute() {
        return route;
    }

    public void setRoute(RouteView route) {
        this.route = route;
    }

    public DestinationsView getAccommodations() {
        return accommodations;
    }

    public void setAccommodations(DestinationsView accommodations) {
        this.accommodations = accommodations;
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

    public String getRegistrationAllowance() {
        return registrationAllowance;
    }

    public void setRegistrationAllowance(String registrationAllowance) {
        this.registrationAllowance = registrationAllowance;
    }

    @Override
    public String getViewType() {
        return "uncompleted-travel-application";
    }
}
