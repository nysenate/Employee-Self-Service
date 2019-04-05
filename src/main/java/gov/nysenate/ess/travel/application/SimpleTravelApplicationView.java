package gov.nysenate.ess.travel.application;

import gov.nysenate.ess.core.client.view.DetailedEmployeeView;
import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.travel.application.allowances.AllowancesView;
import gov.nysenate.ess.travel.application.allowances.lodging.LodgingPerDiemsView;
import gov.nysenate.ess.travel.application.allowances.meal.MealPerDiemsView;
import gov.nysenate.ess.travel.application.allowances.mileage.MileagePerDiemsView;
import gov.nysenate.ess.travel.application.route.SimpleRouteView;

import java.util.List;
import java.util.stream.Collectors;

import static java.time.format.DateTimeFormatter.ISO_DATE;
import static java.time.format.DateTimeFormatter.ISO_DATE_TIME;

public class SimpleTravelApplicationView implements ViewObject {

    private String id;
    private String versionId;
    private DetailedEmployeeView traveler;
    private String purposeOfTravel;
    private SimpleRouteView route;
    private AllowancesView allowances;
    private MealPerDiemsView mealPerDiems;
    private LodgingPerDiemsView lodgingPerDiems;
    private MileagePerDiemsView mileagePerDiems;
    private String submittedDateTime;
    private String modifiedDateTime;
    private DetailedEmployeeView modifiedBy;
    private List<TravelAttachmentView> attachments;

    private String startDate;
    private String endDate;

    private String mileageAllowance;
    private String mealAllowance;
    private String lodgingAllowance;
    private String tollsAllowance;
    private String parkingAllowance;
    private String trainAndPlaneAllowance;
    private String alternateTransportationAllowance;
    private String registrationAllowance;
    private String transportationAllowance;
    private String tollsAndParkingAllowance;
    private String totalAllowance;


    public SimpleTravelApplicationView() {
    }

    public SimpleTravelApplicationView(TravelApplication app) {
        id = String.valueOf(app.getAppId());
        versionId = String.valueOf(app.getVersionId());
        traveler = new DetailedEmployeeView(app.getTraveler());
        purposeOfTravel = app.getPurposeOfTravel();
        route = new SimpleRouteView(app.getRoute());
        allowances = new AllowancesView(app.getAllowances());
        submittedDateTime = app.getSubmittedDateTime() == null ? null : app.getSubmittedDateTime().format(ISO_DATE_TIME);
        modifiedDateTime = app.getModifiedDateTime() == null ? null : app.getModifiedDateTime().format(ISO_DATE_TIME);
        modifiedBy = app.getModifiedBy() == null ? null : new DetailedEmployeeView(app.getModifiedBy());
        attachments = app.getAttachments().stream().map(TravelAttachmentView::new).collect(Collectors.toList());

        startDate = app.startDate() == null ? "" : app.startDate().format(ISO_DATE);
        endDate = app.endDate() == null ? "" : app.endDate().format(ISO_DATE);
        mealPerDiems = new MealPerDiemsView(app.getRoute().mealAllowances());
        lodgingPerDiems = new LodgingPerDiemsView(app.getRoute().lodgingAllowances());
        mileagePerDiems = new MileagePerDiemsView(app.getRoute().mileageAllowances());

        mileageAllowance = app.mileageAllowance().toString();
        mealAllowance = app.mealAllowance().toString();
        lodgingAllowance = app.lodgingAllowance().toString();
        tollsAllowance = app.tollsAllowance().toString();
        parkingAllowance = app.parkingAllowance().toString();
        trainAndPlaneAllowance = app.trainAndPlaneAllowance().toString();
        alternateTransportationAllowance = app.alternateTransportationAllowance().toString();
        registrationAllowance = app.registrationAllowance().toString();
        transportationAllowance = app.transportationAllowance().toString();
        tollsAndParkingAllowance = app.tollsAndParkingAllowance().toString();
        totalAllowance = app.totalAllowance().toString();
    }

    public String getId() {
        return id;
    }

    public String getVersionId() {
        return versionId;
    }

    public DetailedEmployeeView getTraveler() {
        return traveler;
    }

    public String getPurposeOfTravel() {
        return purposeOfTravel;
    }

    public SimpleRouteView getRoute() {
        return route;
    }

    public AllowancesView getAllowances() {
        return allowances;
    }

    public MealPerDiemsView getMealPerDiems() {
        return mealPerDiems;
    }

    public LodgingPerDiemsView getLodgingPerDiems() {
        return lodgingPerDiems;
    }

    public MileagePerDiemsView getMileagePerDiems() {
        return mileagePerDiems;
    }

    public String getSubmittedDateTime() {
        return submittedDateTime;
    }

    public String getModifiedDateTime() {
        return modifiedDateTime;
    }

    public DetailedEmployeeView getModifiedBy() {
        return modifiedBy;
    }

    public List<TravelAttachmentView> getAttachments() {
        return attachments;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
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

    public String getTrainAndPlaneAllowance() {
        return trainAndPlaneAllowance;
    }

    public String getAlternateTransportationAllowance() {
        return alternateTransportationAllowance;
    }

    public String getRegistrationAllowance() {
        return registrationAllowance;
    }

    public String getTransportationAllowance() {
        return transportationAllowance;
    }

    public String getTollsAndParkingAllowance() {
        return tollsAndParkingAllowance;
    }

    public String getTotalAllowance() {
        return totalAllowance;
    }

    @Override
    public String getViewType() {
        return "simple-travel-application";
    }
}
