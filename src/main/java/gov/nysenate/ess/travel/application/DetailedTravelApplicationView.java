package gov.nysenate.ess.travel.application;

import gov.nysenate.ess.core.client.view.DetailedEmployeeView;
import gov.nysenate.ess.core.client.view.EmployeeView;

public class DetailedTravelApplicationView extends TravelApplicationView {

    private DetailedEmployeeView traveler;
    private DetailedEmployeeView submitter;

    public DetailedTravelApplicationView() {
    }

    public DetailedTravelApplicationView(TravelApplication app) {
        super(app);
        traveler = new DetailedEmployeeView(app.getTraveler());
        submitter = new DetailedEmployeeView(app.getSubmitter());
    }

    @Override
    public DetailedEmployeeView getTraveler() {
        return traveler;
    }

    @Override
    public DetailedEmployeeView getSubmitter() {
        return submitter;
    }

    @Override
    public String getViewType() {
        return "detailed-travel-application";
    }
}
