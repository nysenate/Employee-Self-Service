package gov.nysenate.ess.travel.api.application;

import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.travel.request.app.TravelApplication;

import static java.time.format.DateTimeFormatter.ISO_DATE;

public class TravelApplicationSummaryView implements ViewObject {

    private int id;
    private String travelerName;
    private String startDate;
    private String endDate;
    private String destinationSummary;
    private String totalAllowance;
    private TravelApplicationStatusView status;

    public TravelApplicationSummaryView(TravelApplication app) {
        this.id = app.getAppId();
        this.travelerName = app.getTraveler().getFullName();
        this.startDate = app.startDate() == null ? null : app.startDate().format(ISO_DATE);
        this.endDate = app.endDate() == null ? null : app.endDate().format(ISO_DATE);
        this.destinationSummary = app.getRoute().destinationSummary();
        this.totalAllowance = app.totalAllowance().toString();
        this.status = new TravelApplicationStatusView(app.getStatus());
    }

    public int getId() {
        return id;
    }

    public String getTravelerName() {
        return travelerName;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public String getDestinationSummary() {
        return destinationSummary;
    }

    public String getTotalAllowance() {
        return totalAllowance;
    }

    public TravelApplicationStatusView getStatus() {
        return status;
    }

    @Override
    public String getViewType() {
        return "Travel Application Summary";
    }
}
