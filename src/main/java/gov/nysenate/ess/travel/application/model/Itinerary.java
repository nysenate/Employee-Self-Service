package gov.nysenate.ess.travel.application.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import gov.nysenate.ess.core.model.unit.Address;
import gov.nysenate.ess.travel.allowance.mileage.model.Leg;
import gov.nysenate.ess.travel.allowance.mileage.model.Route;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
     * Calculates the route of travel that is eligible for mileage reimbursement.
     * @return
     */
    public Route getReimbursableRoute() {
        Set<Leg> outboundLegs = new HashSet<>();
        Address from = getOrigin();
        for (Address to : destinationAddresses()) {
            outboundLegs.add(new Leg(from, to));
            from = to;
        }
        Set<Leg> returnLegs = Sets.newHashSet(new Leg(lastDestination().getAddress(), getOrigin()));
        return new Route(outboundLegs, returnLegs);
    }

    private Set<Address> destinationAddresses() {
        return destinations.stream()
                .map(TravelDestination::getAddress)
                .collect(Collectors.toSet());
    }

    /**
     * The planned start date of the trip.
     * @return
     */
    public LocalDate startDate() {
        return firstDestination().getArrivalDate();
    }

    private TravelDestination firstDestination() {
       return getDestinations().get(0);
    }

    /**
     * The planned end date of the trip.
     * @return
     */
    public LocalDate endDate() {
       return lastDestination().getDepartureDate();
    }

    private TravelDestination lastDestination() {
        return getDestinations().get(getDestinations().size() - 1);
    }

    /**
     * @return The origin {@link Address}.
     */
    public Address getOrigin() {
        return origin;
    }

    /**
     * @return A ImmutableList of {@link TravelDestination}
     */
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
