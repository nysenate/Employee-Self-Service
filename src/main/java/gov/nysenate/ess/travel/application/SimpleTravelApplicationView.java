package gov.nysenate.ess.travel.application;

import gov.nysenate.ess.core.client.view.DetailedEmployeeView;
import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.travel.application.allowances.AllowancesView;
import gov.nysenate.ess.travel.application.allowances.lodging.LodgingAllowancesView;
import gov.nysenate.ess.travel.application.allowances.meal.MealAllowancesView;
import gov.nysenate.ess.travel.application.allowances.mileage.MileageAllowancesView;
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
    private MealAllowancesView mealAllowances;
    private LodgingAllowancesView lodgingAllowances;
    private MileageAllowancesView mileageAllowances;
    private String submittedDateTime;
    private String modifiedDateTime;
    private DetailedEmployeeView modifiedBy;
    private List<TravelAttachmentView> attachments;
    private String startDate;
    private String endDate;

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
        mealAllowances = new MealAllowancesView(app.getRoute().mealAllowances());
        lodgingAllowances = new LodgingAllowancesView(app.getRoute().lodgingAllowances());
        mileageAllowances = new MileageAllowancesView(app.getRoute().mileageAllowances());
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

    public MealAllowancesView getMealAllowances() {
        return mealAllowances;
    }

    public LodgingAllowancesView getLodgingAllowances() {
        return lodgingAllowances;
    }

    public MileageAllowancesView getMileageAllowances() {
        return mileageAllowances;
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

    @Override
    public String getViewType() {
        return "simple-travel-application";
    }
}
