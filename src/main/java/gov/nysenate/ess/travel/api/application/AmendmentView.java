package gov.nysenate.ess.travel.api.application;

import gov.nysenate.ess.core.client.view.DetailedEmployeeView;
import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.travel.request.allowances.mileage.MileagePerDiemsView;
import gov.nysenate.ess.travel.request.app.TravelApplication;
import gov.nysenate.ess.travel.request.attachment.Attachment;
import gov.nysenate.ess.travel.request.address.TravelAddress;
import gov.nysenate.ess.travel.request.allowances.AllowancesView;
import gov.nysenate.ess.travel.request.allowances.lodging.LodgingPerDiemsView;
import gov.nysenate.ess.travel.request.allowances.meal.MealPerDiemsView;
import gov.nysenate.ess.travel.request.route.RouteView;
import gov.nysenate.ess.travel.request.route.destination.Destination;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.time.format.DateTimeFormatter.ISO_DATE;
import static java.time.format.DateTimeFormatter.ISO_DATE_TIME;

public class AmendmentView implements ViewObject {

    private PurposeOfTravelView purposeOfTravel;
    private RouteView route;
    private AllowancesView allowances;
    private MealPerDiemsView mealPerDiems;
    private LodgingPerDiemsView lodgingPerDiems;
    private MileagePerDiemsView mileagePerDiems;
    private String createdDateTime;
    private DetailedEmployeeView createdBy;
    private List<AttachmentView> attachments;

    private String startDate;
    private String endDate;

    // Summary of net allowances.
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

    public AmendmentView() {
    }

    public AmendmentView(TravelApplication app) {
        purposeOfTravel = new PurposeOfTravelView(app.getPurposeOfTravel());
        route = new RouteView(app.getRoute());
        allowances = new AllowancesView(app.getAllowances());
        mealPerDiems = new MealPerDiemsView(app.getMealPerDiems());
        lodgingPerDiems = new LodgingPerDiemsView(app.getLodgingPerDiems());
        mileagePerDiems = new MileagePerDiemsView(app.getMileagePerDiems());
        createdDateTime = app.getCreatedDateTime() == null
                ? null
                : app.getCreatedDateTime().format(ISO_DATE_TIME);
        createdBy = app.getCreatedBy() == null
                ? null
                : new DetailedEmployeeView(app.getCreatedBy());
        attachments = app.getAttachments().stream().map(AttachmentView::new).collect(Collectors.toList());

        startDate = app.startDate() == null ? "" : app.startDate().format(ISO_DATE);
        endDate = app.endDate() == null ? "" : app.endDate().format(ISO_DATE);

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
            TravelAddress address = destinations.get(0).getAddress();
            destinationSummary = address.getSummary();
            if (destinations.size() > 1) {
                destinationSummary += " ...";
            }
        }
    }

    public void updateTravelApplication(TravelApplication app) {
        List<Attachment> attachments = this.attachments == null || this.attachments.isEmpty()
                ? new ArrayList<>()
                : this.attachments.stream()
                .map(AttachmentView::toAttachment)
                .collect(Collectors.toList());

        app.setAttachments(attachments);
        app.setPurposeOfTravel(purposeOfTravel.toPurposeOfTravel());
        app.setRoute(route.toRoute());
        app.setAllowances(allowances.toAllowances());
        app.setAttachments(attachments);
        app.setCreatedDateTime(createdDateTime == null ? null : LocalDateTime.parse(createdDateTime, ISO_DATE_TIME));
        app.setCreatedBy(createdBy == null ? null : createdBy.toEmployee());
        app.setMealPerDiems(mealPerDiems.toMealPerDiems());
        app.setLodgingPerDiems(lodgingPerDiems.toLodgingPerDiems());
        app.setMileagePerDiems(mileagePerDiems.toMileagePerDiems());
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

    public String getCreatedDateTime() {
        return createdDateTime;
    }

    public DetailedEmployeeView getCreatedBy() {
        return createdBy;
    }

    public List<AttachmentView> getAttachments() {
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

    public MileagePerDiemsView getMileagePerDiems() {
        return mileagePerDiems;
    }

    @Override
    public String getViewType() {
        return "travel-application-amendment";
    }
}
