package gov.nysenate.ess.travel.application.route;

import com.google.common.collect.ImmutableList;
import gov.nysenate.ess.core.model.unit.Address;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public class Route {

    public static final Route EMPTY_ROUTE = new Route(ImmutableList.of(), ImmutableList.of());

    private final ImmutableList<Leg> outgoingLegs;
    private final ImmutableList<Leg> returnLegs;

    public Route(List<Leg> outgoingLegs, List<Leg> returnLegs) {
        this.outgoingLegs = ImmutableList.copyOf(outgoingLegs);
        this.returnLegs = ImmutableList.copyOf(returnLegs);
    }

    public Address origin() {
        if (getOutgoingLegs().size() > 0) {
            return getOutgoingLegs().get(0).getFrom();
        }
        return new Address();
    }

    /**
     * The date where travel started for this Route.
     * @return
     */
    public LocalDate startDate() {
        // TODO Tests
        if (getOutgoingLegs().size() == 0) {
            return null;
        }
        return getOutgoingLegs().stream()
                .map(Leg::getTravelDate)
                .min(LocalDate::compareTo)
                .get();
    }

    /**
     * The last day of travel for this Route.
     * @return
     */
    public LocalDate endDate() {
        // TODO Tests
        if (getReturnLegs().size() == 0) {
            return null;
        }
        return getReturnLegs().stream()
                .map(Leg::getTravelDate)
                .max(LocalDate::compareTo)
                .get();
    }

    /**
     * The route is constructed by setting the outgoing legs separately from the return legs.
     * However, both need to be set for the route to be completely initialized.
     *
     * This method checks if both outgoing and return legs have been set.
     * @return
     */
    public boolean isComplete() {
        return getOutgoingLegs().size() > 1 && getReturnLegs().size() > 1;
    }

    public ImmutableList<Leg> getOutgoingLegs() {
        return outgoingLegs;
    }

    public ImmutableList<Leg> getReturnLegs() {
        return returnLegs;
    }

    @Override
    public String toString() {
        return "Route{" +
                "outgoingLegs=" + outgoingLegs +
                ", returnLegs=" + returnLegs +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Route route = (Route) o;
        return Objects.equals(outgoingLegs, route.outgoingLegs) &&
                Objects.equals(returnLegs, route.returnLegs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(outgoingLegs, returnLegs);
    }
}
