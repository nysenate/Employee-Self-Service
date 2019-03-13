package gov.nysenate.ess.travel.application.route;

import com.google.common.collect.ImmutableList;
import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.travel.application.route.destination.DestinationView;

import java.util.List;
import java.util.stream.Collectors;

public class RouteView implements ViewObject{

    private List<LegView> outboundLegs;
    private List<LegView> returnLegs;
    private DestinationView origin;
    private List<DestinationView> destinations;
    private String totalMiles;
    private String mileageExpense;

    public RouteView() {
    }
    public RouteView(Route route) {
        outboundLegs = route.getOutgoingLegs().stream()
                .map(LegView::new)
                .collect(Collectors.toList());
        returnLegs = route.getReturnLegs().stream()
                .map(LegView::new)
                .collect(Collectors.toList());
        origin = route.origin() == null ? null : new DestinationView(route.origin());
        totalMiles = String.valueOf(route.totalMiles());
        mileageExpense = route.mileageExpense().toString();
        destinations = route.destinations().stream()
                .map(DestinationView::new)
                .collect(Collectors.toList());
    }

    public Route toRoute() {
        return new Route(outboundLegs.stream().map(LegView::toLeg).collect(ImmutableList.toImmutableList()),
                returnLegs.stream().map(LegView::toLeg).collect(ImmutableList.toImmutableList()));
    }

    public List<LegView> getOutboundLegs() {
        return outboundLegs;
    }

    public List<LegView> getReturnLegs() {
        return returnLegs;
    }

    public DestinationView getOrigin() {
        return origin;
    }

    public List<DestinationView> getDestinations() {
        return destinations;
    }

    public String getTotalMiles() {
        return totalMiles;
    }

    public String getMileageExpense() {
        return mileageExpense;
    }

    @Override
    public String getViewType() {
        return "route";
    }
}
