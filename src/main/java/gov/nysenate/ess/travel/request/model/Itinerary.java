package gov.nysenate.ess.travel.request.model;

import com.google.common.collect.Lists;
import gov.nysenate.ess.core.model.unit.Address;

import java.util.List;

/**
 * The Itinerary of a TravelRequest.
 * Contains the origin address and all destination address's with arrival and departure times.
 *
 * TODO What if car pooling? Origin = null?
 */
public class Itinerary {

    private Address origin;
    private List<TravelDestination> destinations;

    public Itinerary(Address origin, List<TravelDestination> destinations) {
        this.origin = origin;
        this.destinations = destinations;
    }

    /**
     * The travel route represented by this Itinerary.
     * @return A list of addresses in the order they will be traveled.
     *
     * TODO Add tests and handle edge cases, e.g. what if car pooling and origin is null?
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
