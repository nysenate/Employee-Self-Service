package gov.nysenate.ess.travel.application;

import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.travel.application.allowances.AllowancesView;
import gov.nysenate.ess.travel.application.route.RouteView;

/**
 * Similar to {@link TravelApplicationView} except this class
 * uses id's for employees instead of full employee views. It also omits some
 * fields not relevant to an uncompleted application, such as submitted date time.
 *
 * This is used when persisting an uncompleted TravelApplication as a json object.
 */
public class UncompletedTravelApplicationView implements ViewObject {

    int travelerId;
    String purposeOfTravel;
    RouteView route;
    AllowancesView allowances;

    public UncompletedTravelApplicationView() {
    }

    public UncompletedTravelApplicationView(TravelApplication app) {
        travelerId = app.getTraveler().getEmployeeId();
        purposeOfTravel = app.getPurposeOfTravel();
        route = new RouteView(app.getRoute());
        allowances = new AllowancesView(app.getAllowances());
    }

    public int getTravelerId() {
        return travelerId;
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

    @Override
    public String getViewType() {
        return "uncompleted-travel-application";
    }
}
