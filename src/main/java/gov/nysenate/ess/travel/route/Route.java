package gov.nysenate.ess.travel.route;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Streams;
import gov.nysenate.ess.core.model.unit.Address;
import gov.nysenate.ess.travel.utils.Dollars;

/**
 * The Route is responsible for calculating a mileage allowance.
 * It uses a collection of {@link Leg} representing the planned path of travel
 * and user defined options.
 */
public class Route {

    public static final Route EMPTY_ROUTE = new Route(ImmutableList.of(), ImmutableList.of(),
            new Dollars("0"), false);

    private static final double MILEAGE_THRESHOLD = 35.0;
    private final ImmutableList<Leg> outgoingLegs;
    private final ImmutableList<Leg> returnLegs;
    private final Dollars mileageRate;
    private final boolean isMileageRequested;

    public Route(ImmutableList<Leg> outgoingLegs, ImmutableList<Leg> returnLegs,
                 Dollars mileageRate, boolean isMileageRequested) {
        this.outgoingLegs = outgoingLegs;
        this.returnLegs = returnLegs;
        this.mileageRate = mileageRate;
        this.isMileageRequested = isMileageRequested;
    }

    /**
     * Calculate and return the mileage allowance for this Route.
     * @return
     */
    public Dollars mileageAllowance() {
        if (qualifiesForReimbursement()) {
            return getMileageRate().multiply(new Dollars(totalQualifyingMiles()));
        }
        else {
            return Dollars.ZERO;
        }
    }

    public Address origin() {
        if (getOutgoingLegs().size() > 0) {
            return getOutgoingLegs().get(0).getFrom();
        }
        return new Address();
    }

    private boolean qualifiesForReimbursement() {
        return isMileageRequested() && outboundQualifyingMiles() > MILEAGE_THRESHOLD;
    }

    private double outboundQualifyingMiles() {
        return totalMilesOf(qualifyingLegs(getOutgoingLegs()));
    }

    private double totalMilesOf(ImmutableList<Leg> legs) {
        return legs.stream()
                .mapToDouble(Leg::getMiles)
                .sum();
    }

    private ImmutableList<Leg> qualifyingLegs(ImmutableList<Leg> legs) {
        return legs.stream()
                .filter(Leg::qualifies)
                .collect(ImmutableList.toImmutableList());
    }

    private double totalQualifyingMiles() {
        return totalMilesOf(qualifyingLegs(allLegs()));
    }

    private ImmutableList<Leg> allLegs() {
        return Streams.concat(getOutgoingLegs().stream(), getReturnLegs().stream())
                .collect(ImmutableList.toImmutableList());
    }

    ImmutableList<Leg> getOutgoingLegs() {
        return outgoingLegs;
    }

    ImmutableList<Leg> getReturnLegs() {
        return returnLegs;
    }

    Dollars getMileageRate() {
        return mileageRate;
    }

    boolean isMileageRequested() {
        return isMileageRequested;
    }

}
