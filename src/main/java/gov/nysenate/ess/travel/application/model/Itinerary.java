package gov.nysenate.ess.travel.application.model;

import com.google.common.collect.Lists;
import gov.nysenate.ess.core.model.unit.Address;

import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * The Itinerary of a TravelApplication.
 * Contains the origin address and all destination address's with arrival and departure times.
 */
public class Itinerary {

    private Address origin;
    private List<TravelDestination> destinations;

    public Itinerary(Address origin, List<TravelDestination> destinations) {
        // TODO check address's are not empty
        checkArgument(!destinations.isEmpty(), "Itinerary requires a non empty destination list.");
        this.origin = checkNotNull(origin, "Itinerary requires non null origin");
        this.destinations = destinations;
    }

    /**
     * The travel route represented by this Itinerary.
     * @return A list of addresses in the order they will be traveled.
     */
    public List<Address> travelRoute() {
        List<Address> route = Lists.newArrayList(origin);
        for (TravelDestination destination : destinations) {
            route.add(destination.getAddress());
        }
        route.add(origin);
        return route;
    }

    public Address getOrigin() {
        return origin;
    }

    public List<TravelDestination> getTravelDestinations() {
        return destinations;
    }
}
