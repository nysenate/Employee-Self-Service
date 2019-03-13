package gov.nysenate.ess.travel.application;

import gov.nysenate.ess.core.client.view.DetailedEmployeeView;
import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.travel.application.allowances.AllowancesView;
import gov.nysenate.ess.travel.application.route.RouteView;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static java.time.format.DateTimeFormatter.ISO_DATE;
import static java.time.format.DateTimeFormatter.ISO_DATE_TIME;

public class TravelApplicationView implements ViewObject {

    String id;
    String versionId;
    DetailedEmployeeView traveler;
    String purposeOfTravel;
    RouteView route;
    AllowancesView allowances;
    String submittedDateTime;
    String modifiedDateTime;
    DetailedEmployeeView modifiedBy;
    List<TravelAttachmentView> attachments;

    String startDate;
    String endDate;

    public TravelApplicationView() {
    }

    public TravelApplicationView(TravelApplication app) {
        id = String.valueOf(app.getAppId());
        versionId = String.valueOf(app.getVersionId());
        traveler = new DetailedEmployeeView(app.getTraveler());
        purposeOfTravel = app.getPurposeOfTravel();
        route = new RouteView(app.getRoute());
        allowances = new AllowancesView(app.getAllowances());
        submittedDateTime = app.getSubmittedDateTime() == null ? null : app.getSubmittedDateTime().format(ISO_DATE_TIME);
        modifiedDateTime = app.getModifiedDateTime() == null ? null : app.getModifiedDateTime().format(ISO_DATE_TIME);
        modifiedBy = app.getModifiedBy() == null ? null : new DetailedEmployeeView(app.getModifiedBy());
        attachments = app.getAttachments().stream().map(TravelAttachmentView::new).collect(Collectors.toList());

        startDate = app.startDate() == null ? "" : app.startDate().format(ISO_DATE);
        endDate = app.endDate() == null ? "" : app.endDate().format(ISO_DATE);
    }

    public TravelApplication toTravelApplication() {
        int appId = (id == null || id.isEmpty()) ? 0 : Integer.valueOf(id);
        int appVersionId = (versionId == null || versionId.isEmpty()) ? 0 : Integer.valueOf(versionId);
        TravelApplication app = new TravelApplication(appId, appVersionId, traveler.toEmployee());
        app.setPurposeOfTravel(purposeOfTravel);
        if (route != null) {
            app.setRoute(route.toRoute());
        }
        app.setAllowances(allowances.toAllowances());
        app.setSubmittedDateTime(submittedDateTime == null ? null : LocalDateTime.parse(submittedDateTime, ISO_DATE_TIME));
        app.setModifiedDateTime(modifiedDateTime == null ? null : LocalDateTime.parse(modifiedDateTime, ISO_DATE_TIME));
        app.setModifiedBy(modifiedBy == null ? null : modifiedBy.toEmployee());
        app.setAttachments(attachments.stream().map(TravelAttachmentView::toTravelAttachment).collect(Collectors.toList()));
        return app;
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

    public RouteView getRoute() {
        return route;
    }

    public AllowancesView getAllowances() {
        return allowances;
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
        return "travel-application";
    }
}
