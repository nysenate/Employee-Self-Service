package gov.nysenate.ess.travel.allowance.mileage.model;

import com.google.common.collect.ImmutableSet;

import java.util.Objects;
import java.util.Set;

/**
 * Contains collections containing the outbound and return legs of a travel request.
 */
public class Route {

    // Legs making up the trip to the destinations.
    private final ImmutableSet<Leg> outboundLegs;
    // Legs making up the trip back home. Likely only contains a single Leg.
    private final ImmutableSet<Leg> returnLegs;

    public Route(Set<Leg> outboundLegs, Set<Leg> returnLegs) {
        this.outboundLegs = ImmutableSet.copyOf(outboundLegs);
        this.returnLegs = ImmutableSet.copyOf(returnLegs);
    }

    public ImmutableSet<Leg> getOutboundLegs() {
        return outboundLegs;
    }

    public ImmutableSet<Leg> getReturnLegs() {
        return returnLegs;
    }

    @Override
    public String toString() {
        return "Route{" +
                "outboundLegs=" + outboundLegs +
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
