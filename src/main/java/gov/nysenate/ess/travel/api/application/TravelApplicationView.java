package gov.nysenate.ess.travel.api.application;

import gov.nysenate.ess.core.client.view.DetailedEmployeeView;
import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.travel.request.app.TravelApplication;

import java.util.Arrays;
import java.util.List;

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
        traveler = app.getTraveler() == null ? null : new DetailedEmployeeView(app.getTraveler());
        travelerDeptHeadEmpId = app.getTravelerDeptHeadEmpId();
        submittedDateTime = app.getSubmittedDateTime() == null ? null : app.getSubmittedDateTime().format(ISO_DATE_TIME);
        lastModifiedDateTime = app.getModifiedDateTime() == null ? null : app.getModifiedDateTime().format(ISO_DATE_TIME);
        lastModifiedBy = app.getModifiedBy() == null ? null : new DetailedEmployeeView(app.getModifiedBy());
        status = new TravelApplicationStatusView(app.getStatus());
        activeAmendment = new AmendmentView(app);
        amendments = Arrays.asList(activeAmendment);
        this.travelStartDate = app.startDate() == null ? null : app.startDate().format(ISO_DATE);
    }

    public TravelApplication toTravelApplication() {
        TravelApplication app = new TravelApplication.Builder(traveler.toEmployee(), travelerDeptHeadEmpId)
                .withAppId(id)
                .withStatus(status.toTravelApplicationStatus())
                .build();
        activeAmendment.updateTravelApplication(app);
        return app;
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
