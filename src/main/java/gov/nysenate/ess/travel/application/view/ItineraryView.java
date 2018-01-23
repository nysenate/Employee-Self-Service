package gov.nysenate.ess.travel.application.view;

import gov.nysenate.ess.core.client.view.AddressView;
import gov.nysenate.ess.core.client.view.base.ListView;
import gov.nysenate.ess.core.client.view.base.MapView;
import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.travel.application.model.Itinerary;
import gov.nysenate.ess.travel.application.model.TravelDestination;
import gov.nysenate.ess.travel.application.model.TravelDestinationOptions;

import java.util.Map;
import java.util.stream.Collectors;

public class ItineraryView implements ViewObject {

    private AddressView origin;
    private ListView<TravelDestinationView> destinations;

    private ItineraryView() {
    }

    public ItineraryView(Itinerary itinerary) {
        this.origin = new AddressView(itinerary.getOrigin());
        this.destinations = ListView.of(itinerary.getDestinationsToOptions().entrySet().stream()
                .map(TravelDestinationView::new)
                .collect(Collectors.toList()));
    }

    public Itinerary toItinerary() {
        Itinerary itinerary = new Itinerary(origin.toAddress());
        for (TravelDestinationView td: destinations.items) {
            Map.Entry<TravelDestination, TravelDestinationOptions> entry = td.toDestinationOptionsEntry();
            itinerary = itinerary.addDestination(entry.getKey(), entry.getValue());
        }
        return itinerary;
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
