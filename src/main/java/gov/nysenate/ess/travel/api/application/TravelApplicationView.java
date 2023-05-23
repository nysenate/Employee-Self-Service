package gov.nysenate.ess.travel.api.application;

import gov.nysenate.ess.core.client.view.DetailedEmployeeView;
import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.travel.request.amendment.Amendment;
import gov.nysenate.ess.travel.request.app.TravelApplication;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.time.format.DateTimeFormatter.ISO_DATE;
import static java.time.format.DateTimeFormatter.ISO_DATE_TIME;

public class TravelApplicationView implements ViewObject {

    private int id;
    private DetailedEmployeeView traveler;
    private int travelerDeptHeadEmpId;
    private String submittedDateTime;
    private String lastModifiedDateTime;
    private DetailedEmployeeView lastModifiedBy;
    private TravelApplicationStatusView status;
    private AmendmentView activeAmendment;
    private List<AmendmentView> amendments;
    private String travelStartDate;

    public TravelApplicationView() {
    }

    public TravelApplicationView(TravelApplication app) {
        id = app.getAppId();
        traveler = new DetailedEmployeeView(app.getTraveler());
        travelerDeptHeadEmpId = app.getTravelerDeptHeadEmpId();
        submittedDateTime = app.getSubmittedDateTime() == null ? null : app.getSubmittedDateTime().format(ISO_DATE_TIME);
        lastModifiedDateTime = app.activeAmendment() == null ? null : app.activeAmendment().createdDateTime().format(ISO_DATE_TIME);
        lastModifiedBy = app.activeAmendment() == null ? null : new DetailedEmployeeView(app.activeAmendment().createdBy());
        status = new TravelApplicationStatusView(app.status());
        activeAmendment = new AmendmentView(app.activeAmendment());
        amendments = app.getAmendments().stream()
                .map(AmendmentView::new)
                .collect(Collectors.toList());
        this.travelStartDate = app.activeAmendment().startDate().format(ISO_DATE);
    }

    public TravelApplication toTravelApplication() {
        Set<Amendment> amds = amendments.stream()
                .map(AmendmentView::toAmendment)
                .collect(Collectors.toSet());
        return new TravelApplication(id, traveler.toEmployee(), travelerDeptHeadEmpId, status.toTravelApplicationStatus(), amds);
    }

    public int getId() {
        return id;
    }

    public DetailedEmployeeView getTraveler() {
        return traveler;
    }

    public int getTravelerDeptHeadEmpId() {
        return travelerDeptHeadEmpId;
    }

    public String getSubmittedDateTime() {
        return submittedDateTime;
    }

    public String getLastModifiedDateTime() {
        return lastModifiedDateTime;
    }

    public DetailedEmployeeView getLastModifiedBy() {
        return lastModifiedBy;
    }

    public TravelApplicationStatusView getStatus() {
        return status;
    }

    public AmendmentView getActiveAmendment() {
        return activeAmendment;
    }

    public List<AmendmentView> getAmendments() {
        return amendments;
    }

    public String getTravelStartDate() {
        return travelStartDate;
    }

    @Override
    public String getViewType() {
        return "travel-application";
    }
}
