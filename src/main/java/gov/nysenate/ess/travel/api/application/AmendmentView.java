package gov.nysenate.ess.travel.api.application;

import gov.nysenate.ess.core.client.view.DetailedEmployeeView;
import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.travel.request.attachment.Attachment;
import gov.nysenate.ess.travel.request.address.TravelAddress;
import gov.nysenate.ess.travel.request.allowances.AllowancesView;
import gov.nysenate.ess.travel.request.allowances.lodging.LodgingPerDiemsView;
import gov.nysenate.ess.travel.request.allowances.meal.MealPerDiemsView;
import gov.nysenate.ess.travel.request.amendment.Amendment;
import gov.nysenate.ess.travel.request.amendment.Version;
import gov.nysenate.ess.travel.request.route.RouteView;
import gov.nysenate.ess.travel.request.route.destination.Destination;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.time.format.DateTimeFormatter.ISO_DATE;
import static java.time.format.DateTimeFormatter.ISO_DATE_TIME;

public class AmendmentView implements ViewObject {

    private int amendmentId;
    private String version;
    private PurposeOfTravelView purposeOfTravel;
    private RouteView route;
    private AllowancesView allowances;
    private MealPerDiemsView mealPerDiems;
    private LodgingPerDiemsView lodgingPerDiems;
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

    public AmendmentView(Amendment amendment) {
        amendmentId = amendment.amendmentId();
        version = amendment.version().name();
        purposeOfTravel = new PurposeOfTravelView(amendment.purposeOfTravel());
        route = new RouteView(amendment.route());
        allowances = new AllowancesView(amendment.allowances());
        mealPerDiems = new MealPerDiemsView(amendment.mealPerDiems());
        lodgingPerDiems = new LodgingPerDiemsView(amendment.lodgingPerDiems());
        createdDateTime = amendment.createdDateTime() == null
                ? null
                : amendment.createdDateTime().format(ISO_DATE_TIME);
        createdBy = amendment.createdBy() == null
                ? null
                : new DetailedEmployeeView(amendment.createdBy());
        attachments = amendment.attachments().stream().map(AttachmentView::new).collect(Collectors.toList());

        startDate = amendment.startDate() == null ? "" : amendment.startDate().format(ISO_DATE);
        endDate = amendment.endDate() == null ? "" : amendment.endDate().format(ISO_DATE);

        mileageAllowance = amendment.mileageAllowance().toString();
        mealAllowance = amendment.mealAllowance().toString();
        lodgingAllowance = amendment.lodgingAllowance().toString();
        tollsAllowance = amendment.tollsAllowance().toString();
        parkingAllowance = amendment.parkingAllowance().toString();
        trainAndPlaneAllowance = amendment.trainAndPlaneAllowance().toString();
        alternateTransportationAllowance = amendment.alternateTransportationAllowance().toString();
        registrationAllowance = amendment.registrationAllowance().toString();
        transportationAllowance = amendment.transportationAllowance().toString();
        tollsAndParkingAllowance = amendment.tollsAndParkingAllowance().toString();
        totalAllowance = amendment.totalAllowance().toString();

        List<Destination> destinations = amendment.route().destinations();
        if (!destinations.isEmpty()) {
            TravelAddress address = destinations.get(0).getAddress();
            destinationSummary = address.getSummary();
            if (destinations.size() > 1) {
                destinationSummary += " ...";
            }
        }
    }

    public Amendment toAmendment() {
        List<Attachment> attachments = this.attachments == null || this.attachments.isEmpty()
            ? new ArrayList<>()
            : this.attachments.stream()
                .map(AttachmentView::toAttachment)
                .collect(Collectors.toList());
        return new Amendment.Builder()
                .withVersion(Version.valueOf(version))
                .withPurposeOfTravel(purposeOfTravel.toPurposeOfTravel())
                .withRoute(route.toRoute())
                .withAllowances(allowances.toAllowances())
                .withAttachments(attachments)
                .withCreatedDateTime(createdDateTime == null ? null : LocalDateTime.parse(createdDateTime, ISO_DATE_TIME))
                .withCreatedBy(createdBy == null ? null : createdBy.toEmployee())
                .withMealPerDiems(mealPerDiems.toMealPerDiems())
                .withLodgingPerDiems(lodgingPerDiems.toLodgingPerDiems())
                .build();
    }

    public int getAmendmentId() {
        return amendmentId;
    }

    public String getVersion() {
        return version;
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

    @Override
    public String getViewType() {
        return "travel-application-amendment";
    }
}
