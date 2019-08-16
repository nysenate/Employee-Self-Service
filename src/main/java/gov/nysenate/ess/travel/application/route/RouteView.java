package gov.nysenate.ess.travel.application.route;

import gov.nysenate.ess.core.client.view.AddressView;
import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.travel.application.route.destination.DestinationView;

import java.util.List;
import java.util.stream.Collectors;

public class RouteView implements ViewObject {

    private List<LegView> outboundLegs;
    private List<LegView> returnLegs;
    private List<DestinationView> destinations;
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

    @Override
    public String getViewType() {
        return "route";
    }
}
