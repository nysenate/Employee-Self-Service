package gov.nysenate.ess.travel.application.view;

import gov.nysenate.ess.core.client.view.AddressView;
import gov.nysenate.ess.core.client.view.base.ListView;
import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.travel.application.model.Itinerary;

import java.util.stream.Collectors;

public class ItineraryView implements ViewObject {

    private AddressView origin;
    private ListView<TravelDestinationView> destinations;

    public ItineraryView() {
    }

    public ItineraryView(Itinerary itinerary) {
        this.origin = new AddressView(itinerary.getOrigin());
        this.destinations = ListView.of(itinerary.getTravelDestinations().stream()
                .map(TravelDestinationView::new)
                .collect(Collectors.toList()));
    }

    public Itinerary toItinerary() {
        return new Itinerary(origin.toAddress(), destinations.items.stream()
                .map(TravelDestinationView::toTravelDestination)
                .collect(Collectors.toList()));
    }

    public AddressView getOrigin() {
        return origin;
    }

    public ListView<TravelDestinationView> getDestinations() {
        return destinations;
    }

    @Override
    public String getViewType() {
        return "itinerary";
    }
}
