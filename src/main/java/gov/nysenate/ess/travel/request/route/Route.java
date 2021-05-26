package gov.nysenate.ess.travel.request.route;

import com.google.common.collect.ImmutableList;
import gov.nysenate.ess.travel.request.address.TravelAddress;
import gov.nysenate.ess.travel.request.allowances.mileage.MileagePerDiems;
import gov.nysenate.ess.travel.request.route.destination.Destination;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Route {

    public static final Route EMPTY_ROUTE = new Route(ImmutableList.of(), ImmutableList.of());

    private final ImmutableList<Leg> outboundLegs;
    private final ImmutableList<Leg> returnLegs;

    public Route(List<Leg> outboundLegs, List<Leg> returnLegs) {
        this.outboundLegs = ImmutableList.copyOf(outboundLegs);
        this.returnLegs = ImmutableList.copyOf(returnLegs);
    }

    public MileagePerDiems mileagePerDiems() {
        return new MileagePerDiems(getAllLegs());
    }

    public TravelAddress origin() {
        if (getOutboundLegs().size() > 0) {
            return getOutboundLegs().get(0).fromAddress();
        }
        return null;
    }

    /**
     * @return A list of destinations the employee is visiting on the outgoing portion of their trip.
     */
    public List<Destination> destinations() {
        return getOutboundLegs().stream()
                .map(Leg::to)
                .collect(Collectors.toList());
    }

    /**
     * @return The first day of travel.
     */
    public LocalDate startDate() {
        if (getOutboundLegs().size() == 0) {
            return null;
        }
        return getOutboundLegs().stream()
                .map(Leg::travelDate)
                .min(LocalDate::compareTo)
                .get();
    }

    /**
     * @return The last day of travel.
     */
    public LocalDate endDate() {
        if (getReturnLegs().size() == 0) {
            return null;
        }
        return getReturnLegs().stream()
                .map(Leg::travelDate)
                .max(LocalDate::compareTo)
                .get();
    }

    public ImmutableList<Leg> getOutboundLegs() {
        return outboundLegs;
    }

    public ImmutableList<Leg> getReturnLegs() {
        return returnLegs;
    }

    public ImmutableList<Leg> getAllLegs() {
        return Stream.concat(getOutboundLegs().stream(), getReturnLegs().stream())
                .collect(ImmutableList.toImmutableList());
    }

    /**
     * Returns true if this route has any legs with a MethodOfTravel equal to the given MethodOfTravel.
     * @return
     */
    public boolean hasMethodOfTravel(MethodOfTravel mot) {
        return getAllLegs().stream().anyMatch(leg -> leg.methodOfTravel().equals(mot.name()));
    }

    /**
     * Returns a set of all method of travel descriptions used for the given MethodOfTravel.
     * Returns an empty Set if the given MethodOfTravel is not used in this route.
     *
     * Intended to be used to get user entered modes of transportation when they selected
     * OTHER for the MethodOfTravel.
     * @param mot
     * @return
     */
    public Set<String> getMethodOfTravelDescriptions(MethodOfTravel mot) {
        return getAllLegs().stream()
                .filter(leg -> leg.methodOfTravel().equals(mot.name()))
                .map(Leg::methodOfTravelDescription)
                .collect(Collectors.toSet());

    }

    @Override
    public String toString() {
        return "Route{" +
                "outgoingLegs=" + outboundLegs +
                ", returnLegs=" + returnLegs +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Route route = (Route) o;
        return Objects.equals(outboundLegs, route.outboundLegs) &&
                Objects.equals(returnLegs, route.returnLegs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(outboundLegs, returnLegs);
    }
}
