package gov.nysenate.ess.travel.application.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import gov.nysenate.ess.core.model.unit.Address;

import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * The Itinerary of a TravelApplication.
 * Contains the origin address and all destination address's with arrival and departure times.
 */
public final class Itinerary {

    private final Address origin;
    private final ImmutableList<TravelDestination> destinations;

    public Itinerary(Address origin, List<TravelDestination> destinations) {
        checkNotNull(origin, "Itinerary requires non null origin");
        checkNotNull(destinations, "Itinerary requires non null destination list.");
        checkArgument(!origin.isEmpty());
        checkArgument(!destinations.isEmpty(), "Itinerary requires a non empty destination list.");
        this.origin = origin;
        this.destinations = ImmutableList.copyOf(destinations);
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

    public ImmutableList<TravelDestination> getDestinations() {
        return destinations;
    }

    @Override
    public String toString() {
        return "Itinerary{" +
                "origin=" + origin +
                ", destinations=" + destinations +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Itinerary itinerary = (Itinerary) o;

        if (origin != null ? !origin.equals(itinerary.origin) : itinerary.origin != null) return false;
        return destinations != null ? destinations.equals(itinerary.destinations) : itinerary.destinations == null;
    }

    @Override
    public int hashCode() {
        int result = origin != null ? origin.hashCode() : 0;
        result = 31 * result + (destinations != null ? destinations.hashCode() : 0);
        return result;
    }
}
