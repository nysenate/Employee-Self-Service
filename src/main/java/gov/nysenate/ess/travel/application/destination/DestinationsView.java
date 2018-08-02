package gov.nysenate.ess.travel.application.destination;

import gov.nysenate.ess.core.client.view.base.ViewObject;

import java.util.List;
import java.util.stream.Collectors;

public class DestinationsView implements ViewObject {

    List<DestinationView> destinations;

    public DestinationsView() {
    }

    public DestinationsView(Destinations destinations) {
        this.destinations = destinations.getDestinations().stream()
                .map(DestinationView::new)
                .collect(Collectors.toList());
    }

    public Destinations toDestinations() {
        return new Destinations(getDestinations().stream()
                .map(DestinationView::toDestination)
                .collect(Collectors.toList()));
    }

    public List<DestinationView> getDestinations() {
        return destinations;
    }

    @Override
    public String getViewType() {
        return "destinations";
    }
}
