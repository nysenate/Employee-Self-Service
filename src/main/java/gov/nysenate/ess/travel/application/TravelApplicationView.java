package gov.nysenate.ess.travel.application;

import gov.nysenate.ess.core.client.view.DetailedEmployeeView;
import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.core.model.unit.Address;
import gov.nysenate.ess.travel.application.allowances.AllowancesView;
import gov.nysenate.ess.travel.application.allowances.lodging.LodgingPerDiemsView;
import gov.nysenate.ess.travel.application.allowances.meal.MealPerDiemsView;
import gov.nysenate.ess.travel.application.allowances.mileage.MileagePerDiemsView;
import gov.nysenate.ess.travel.application.overrides.perdiem.PerDiemOverridesView;
import gov.nysenate.ess.travel.application.route.RouteView;
import gov.nysenate.ess.travel.application.route.destination.Destination;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static java.time.format.DateTimeFormatter.ISO_DATE;
import static java.time.format.DateTimeFormatter.ISO_DATE_TIME;

public class TravelApplicationView implements ViewObject {

    private int id;
    private int versionId;
    private DetailedEmployeeView traveler;
    private String purposeOfTravel;
    private RouteView route;
    private AllowancesView allowances;
    private PerDiemOverridesView perDiemOverrides;
    private MealPerDiemsView mealPerDiems;
    private LodgingPerDiemsView lodgingPerDiems;
    private MileagePerDiemsView mileagePerDiems;
    private TravelApplicationStatusView status;
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

    /**
     * A summary of the destinations.
     * The city or addr1 of the first destination. Ellipsis added if multiple destinations.
     */
    private String destinationSummary;


    public TravelApplicationView() {
    }

    public TravelApplicationView(TravelApplication app) {
        id = app.getAppId();
        versionId = app.getVersionId();
        traveler = new DetailedEmployeeView(app.getTraveler());
        purposeOfTravel = app.getPurposeOfTravel();
        route = new RouteView(app.getRoute());
        allowances = new AllowancesView(app.getAllowances());
        perDiemOverrides = new PerDiemOverridesView(app.getPerDiemOverrides());
        status = new TravelApplicationStatusView(app.status());
        submittedDateTime = app.getSubmittedDateTime() == null ? null : app.getSubmittedDateTime().format(ISO_DATE_TIME);
        modifiedDateTime = app.getModifiedDateTime() == null ? null : app.getModifiedDateTime().format(ISO_DATE_TIME);
        modifiedBy = app.getModifiedBy() == null ? null : new DetailedEmployeeView(app.getModifiedBy());
        attachments = app.getAttachments().stream().map(TravelAttachmentView::new).collect(Collectors.toList());

        startDate = app.startDate() == null ? "" : app.startDate().format(ISO_DATE);
        endDate = app.endDate() == null ? "" : app.endDate().format(ISO_DATE);
        mealPerDiems = new MealPerDiemsView(app.getRoute().mealPerDiems());
        lodgingPerDiems = new LodgingPerDiemsView(app.getRoute().lodgingPerDiems());
        mileagePerDiems = new MileagePerDiemsView(app.getRoute().mileagePerDiems());

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


        List<Destination> destinations = app.getRoute().destinations();
        if (!destinations.isEmpty()) {
            Address address = destinations.get(0).getAddress();
            String city = address.getCity();
            String addr1 = address.getAddr1();
            destinationSummary = city == null || city.isEmpty() ? addr1 : city;
            if (destinations.size() > 1) {
                destinationSummary += " ...";
            }
        }
    }

    public TravelApplication toTravelApplication() {
        TravelApplication a = new TravelApplication(id, versionId, traveler.toEmployee());
        a.setPurposeOfTravel(purposeOfTravel);
        a.setRoute(route.toRoute());
        a.setAllowances(allowances.toAllowances());
        a.setPerDiemOverrides(perDiemOverrides.toPerDiemOverrides());
        a.setStatus(status.toTravelApplicationStatus());
//        a.setAttachments(); // TODO WIP
        a.setSubmittedDateTime(LocalDateTime.parse(submittedDateTime, ISO_DATE_TIME));
        a.setModifiedDateTime(LocalDateTime.parse(modifiedDateTime, ISO_DATE_TIME));
        a.setModifiedBy(modifiedBy.toEmployee());
        return a;
    }

    public int getId() {
        return id;
    }

    public int getVersionId() {
        return versionId;
    }

    public DetailedEmployeeView getTraveler() {
        return traveler;
    }

    public String getPurposeOfTravel() {
        return purposeOfTravel;
    }

    public RouteView getRoute() {
        return route;
    }

    public AllowancesView getAllowances() {
        return allowances;
    }

    public PerDiemOverridesView getPerDiemOverrides() {
        return perDiemOverrides;
    }

    public TravelApplicationStatusView getStatus() {
        return status;
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

    public String getDestinationSummary() {
        return destinationSummary;
    }

    @Override
    public String getViewType() {
        return "travel-application";
    }
}
