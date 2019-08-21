package gov.nysenate.ess.travel.application.route;

import gov.nysenate.ess.core.client.view.AddressView;
import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.travel.application.allowances.lodging.LodgingPerDiemsView;
import gov.nysenate.ess.travel.application.allowances.meal.MealPerDiemsView;
import gov.nysenate.ess.travel.application.allowances.mileage.MileagePerDiemsView;
import gov.nysenate.ess.travel.application.route.destination.DestinationView;

import java.util.List;
import java.util.stream.Collectors;

public class RouteView implements ViewObject {

    private List<LegView> outboundLegs;
    private List<LegView> returnLegs;
    private List<DestinationView> destinations;
    private MealPerDiemsView mealPerDiems;
    private LodgingPerDiemsView lodgingPerDiems;
    private MileagePerDiemsView mileagePerDiems;
    private AddressView origin;

    public RouteView() {
    }

    public RouteView(Route route) {
        outboundLegs = route.getOutboundLegs().stream()
                .map(LegView::new)
                .collect(Collectors.toList());
        returnLegs = route.getReturnLegs().stream()
                .map(LegView::new)
                .collect(Collectors.toList());
        origin = route.origin() == null ? null : new AddressView(route.origin());
        destinations = route.destinations().stream()
                .map(DestinationView::new)
                .collect(Collectors.toList());
        mealPerDiems = new MealPerDiemsView(route.mealPerDiems());
        lodgingPerDiems = new LodgingPerDiemsView(route.lodgingPerDiems());
        mileagePerDiems = new MileagePerDiemsView(route.mileagePerDiems());
    }

    public Route toRoute() {
        return new Route(
                outboundLegs.stream().map(LegView::toLeg).collect(Collectors.toList()),
                returnLegs.stream().map(LegView::toLeg).collect(Collectors.toList())
        );
    }

    public List<LegView> getOutboundLegs() {
        return outboundLegs;
    }

    public List<LegView> getReturnLegs() {
        return returnLegs;
    }

    public List<DestinationView> getDestinations() {
        return destinations;
    }

    public AddressView getOrigin() {
        return origin;
    }

    public MealPerDiemsView getMealPerDiems() {
        return mealPerDiems;
    }

    public LodgingPerDiemsView getLodgingPerDiems() {
        return lodgingPerDiems;
    }

    public MileagePerDiemsView getMileagePerDiems() {
        return mileagePerDiems;
    }

    @Override
    public String getViewType() {
        return "route";
    }
}
