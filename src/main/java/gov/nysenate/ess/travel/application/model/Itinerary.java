package gov.nysenate.ess.travel.application.model;

import com.google.common.collect.*;
import gov.nysenate.ess.core.model.unit.Address;
import gov.nysenate.ess.travel.allowance.mileage.model.Leg;
import gov.nysenate.ess.travel.allowance.mileage.model.Route;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * The Itinerary of a TravelApplication.
 * Contains the origin address and all destination address's with arrival and departure times.
 */
public final class Itinerary {

    private final Address origin;
    private final ImmutableMap<TravelDestination, TravelDestinationOptions> destinationsToOptions;

    public Itinerary(Address origin) {
        this(origin, ImmutableMap.of());
    }

    private Itinerary(Address origin,
                      ImmutableMap<TravelDestination, TravelDestinationOptions> destinationsToOptions) {
        checkNotNull(origin, "Itinerary requires non null origin");
        checkArgument(!origin.isEmpty());
        checkNotNull(destinationsToOptions, "Itinerary requires non null destinations.");
        this.origin = origin;
        this.destinationsToOptions = destinationsToOptions;
    }

    /**
     * Adds a destination and its options to this Itinerary.
     * Destinations should be added in the order they will be traveled.
     * @param destination
     * @param options
     * @return
     */
    public Itinerary addDestination(TravelDestination destination, TravelDestinationOptions options) {
        return new Itinerary(getOrigin(),
                ImmutableMap.<TravelDestination, TravelDestinationOptions>builder()
                        .putAll(destinationsToOptions)
                        .put(destination, options).build());
    }

    /**
     * Calculates the route of travel that is eligible for mileage reimbursement.
     * @return
     */
    public Route getReimbursableRoute() {
        Set<Leg> outboundLegs = new HashSet<>();
        Address from = getOrigin();
        for (Address to : getReimbursableAddresses()) {
            outboundLegs.add(new Leg(from, to));
            from = to;
        }
        Set<Leg> returnLegs = new HashSet<>();
        if (outboundLegs.size() > 0) {
            returnLegs.add(new Leg(from, getOrigin()));
        }
        return new Route(outboundLegs, returnLegs);
    }

    private Set<Address> getReimbursableAddresses() {
        return destinationsToOptions.entrySet().stream()
                .filter(m -> m.getValue().isMileageReimbursable())
                .map(m -> m.getKey().getAddress())
                .collect(Collectors.toSet());
    }


    /**
     * @return A set of {@link TravelDestination}'s where the traveler
     * has requested lodging reimbursement.
     */
    public Set<TravelDestination> getLodgingRequestedDestinations() {
        return destinationsToOptions.entrySet().stream()
                .filter(m -> m.getValue().isRequestLodging())
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }

    /**
     * @return A set of {@link TravelDestination}'s where the traveler has
     * requested meal reimbursement.
     */
    public Set<TravelDestination> getMealsRequestedDestinations() {
        return destinationsToOptions.entrySet().stream()
                .filter(m -> m.getValue().isRequestMeals())
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }

    /**
     * @return A list of all destinations.
     */
    public List<TravelDestination> getDestinations() {
        return destinationsToOptions.keySet().asList();
    }

    /**
     * The planned start date of the trip.
     * @return
     */
    public LocalDate startDate() {
        return firstDestination().getArrivalDate();
    }

    private TravelDestination firstDestination() {
       return destinationsToOptions.keySet().iterator().next();
    }

    /**
     * The planned end date of the trip.
     */
    public LocalDate endDate() {
       return lastDestination().getDepartureDate();
    }

    private TravelDestination lastDestination() {
        ImmutableSet<TravelDestination> destSet = destinationsToOptions.keySet();
        return destSet.asList().get(destSet.size() - 1);
    }

    /**
     * @return The origin {@link Address}.
     */
    public Address getOrigin() {
        return origin;
    }

    /**
     * Get destinations to options map.
     */
    public ImmutableMap<TravelDestination, TravelDestinationOptions> getDestinationsToOptions() {
        return destinationsToOptions;
    }

    @Override
    public String toString() {
        return "Itinerary{" +
                "origin=" + origin +
                ", destinationsToOptions=" + destinationsToOptions +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Itinerary itinerary = (Itinerary) o;
        return Objects.equals(origin, itinerary.origin) &&
                Objects.equals(destinationsToOptions, itinerary.destinationsToOptions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(origin, destinationsToOptions);
    }
}
