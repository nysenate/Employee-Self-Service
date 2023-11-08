package gov.nysenate.ess.travel.application;

import com.google.common.collect.Lists;
import gov.nysenate.ess.core.client.view.DetailedEmployeeView;
import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.travel.application.address.GoogleAddress;
import gov.nysenate.ess.travel.application.allowances.AllowancesView;
import gov.nysenate.ess.travel.application.allowances.lodging.LodgingPerDiemsView;
import gov.nysenate.ess.travel.application.allowances.meal.MealPerDiemsView;
import gov.nysenate.ess.travel.application.route.RouteView;
import gov.nysenate.ess.travel.application.route.destination.Destination;

import java.util.List;
import java.util.stream.Collectors;

import static java.time.format.DateTimeFormatter.ISO_DATE;
import static java.time.format.DateTimeFormatter.ISO_DATE_TIME;

public class TravelApplicationView implements ViewObject {

    private int id;
    private DetailedEmployeeView traveler;
    private PurposeOfTravelView purposeOfTravel;
    private RouteView route;
    private AllowancesView allowances;
    private MealPerDiemsView mealPerDiems;
    private LodgingPerDiemsView lodgingPerDiems;
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
        traveler = new DetailedEmployeeView(app.getTraveler());
        purposeOfTravel = new PurposeOfTravelView(app.activeAmendment().purposeOfTravel());
        route = new RouteView(app.activeAmendment().route());
        allowances = new AllowancesView(app.activeAmendment().allowances());
        mealPerDiems = new MealPerDiemsView(app.activeAmendment().mealPerDiems());
        lodgingPerDiems = new LodgingPerDiemsView(app.activeAmendment().lodgingPerDiems());
        status = new TravelApplicationStatusView(app.activeAmendment().status());
        submittedDateTime = app.getSubmittedDateTime() == null ? null : app.getSubmittedDateTime().format(ISO_DATE_TIME);
        modifiedDateTime = app.getModifiedDateTime() == null ? null : app.getModifiedDateTime().format(ISO_DATE_TIME);
        modifiedBy = app.getSubmittedBy() == null ? null : new DetailedEmployeeView(app.getSubmittedBy());
        attachments = app.activeAmendment().attachments().stream().map(TravelAttachmentView::new).collect(Collectors.toList());

        startDate = app.activeAmendment().startDate() == null ? "" : app.activeAmendment().startDate().format(ISO_DATE);
        endDate = app.activeAmendment().endDate() == null ? "" : app.activeAmendment().endDate().format(ISO_DATE);

        mileageAllowance = app.activeAmendment().mileageAllowance().toString();
        mealAllowance = app.activeAmendment().mealAllowance().toString();
        lodgingAllowance = app.activeAmendment().lodgingAllowance().toString();
        tollsAllowance = app.activeAmendment().tollsAllowance().toString();
        parkingAllowance = app.activeAmendment().parkingAllowance().toString();
        trainAndPlaneAllowance = app.activeAmendment().trainAndPlaneAllowance().toString();
        alternateTransportationAllowance = app.activeAmendment().alternateTransportationAllowance().toString();
        registrationAllowance = app.activeAmendment().registrationAllowance().toString();
        transportationAllowance = app.activeAmendment().transportationAllowance().toString();
        tollsAndParkingAllowance = app.activeAmendment().tollsAndParkingAllowance().toString();
        totalAllowance = app.activeAmendment().totalAllowance().toString();


        List<Destination> destinations = app.activeAmendment().route().destinations();
        if (!destinations.isEmpty()) {
            GoogleAddress address = destinations.get(0).getAddress();
            String name = address.getName();
            String city = address.getCity();
            String addr1 = address.getAddr1();
            destinationSummary = name.isEmpty() ? addr1.isEmpty() ? city : addr1 : name;
            if (destinations.size() > 1) {
                destinationSummary += " ...";
            }
        }
    }

    public TravelApplication toTravelApplication() {
        Amendment amd = new Amendment.Builder()
                .withAmendmentId(0)
                .withVersion(Version.A)
                .withPurposeOfTravel(purposeOfTravel.toPurposeOfTravel())
                .withRoute(route.toRoute())
                .withAllowances(allowances.toAllowances())
                .withMealPerDiems(mealPerDiems.toMealPerDiems())
                .withLodgingPerDiems(lodgingPerDiems.toLodgingPerDiems())
                .withStatus(status.toTravelApplicationStatus())
                .withCreatedBy(modifiedBy.toEmployee())
                // TODO modified time
                .build();
        return new TravelApplication(id, traveler.toEmployee(), Lists.newArrayList(amd));
    }

    public int getId() {
        return id;
    }

    public DetailedEmployeeView getTraveler() {
        return traveler;
    }

    public PurposeOfTravelView getPurposeOfTravel() {
        return purposeOfTravel;
    }

    public RouteView getRoute() {
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

    public TravelApplicationStatusView getStatus() {
        return status;
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
