package gov.nysenate.ess.travel.route;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Streams;
import gov.nysenate.ess.core.model.unit.Address;
import gov.nysenate.ess.travel.utils.Dollars;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * The Route is responsible for calculating a mileage allowance.
 * It uses a collection of {@link Leg} representing the planned path of travel
 * and user defined options.
 *
 * The return trip goes from the last destination back to the origin, stopping
 * at all destinations where the Mode of Transportation changed. This is necessary to
 * correctly count miles if the traveler only drives part way.
 *
 * See RouteTest for examples.
 */
public class Route {

    // TODO Remove this.??
    public static final Route EMPTY_ROUTE = new Route(ImmutableList.of(), ImmutableList.of(),
            new BigDecimal("0"));

    private static final double MILEAGE_THRESHOLD = 35.0;

    private final ImmutableList<Leg> outgoingLegs;
    private final ImmutableList<Leg> returnLegs;
    private final BigDecimal mileageRate;

    public Route(ImmutableList<Leg> outgoingLegs, ImmutableList<Leg> returnLegs, BigDecimal mileageRate) {
        this.outgoingLegs = outgoingLegs;
        this.returnLegs = returnLegs;
        this.mileageRate = mileageRate;
    }

    /**
     * Calculate and return the mileage allowance for this Route.
     * @return
     */
    public Dollars mileageAllowance() {
        if (qualifiesForReimbursement()) {
            return new Dollars(getMileageRate().multiply(new BigDecimal(totalQualifyingMiles())));
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

    public ImmutableList<Leg> getOutgoingLegs() {
        return outgoingLegs;
    }

    public ImmutableList<Leg> getReturnLegs() {
        return returnLegs;
    }

    public BigDecimal getMileageRate() {
        return mileageRate;
    }

    private boolean qualifiesForReimbursement() {
        return outboundQualifyingMiles() > MILEAGE_THRESHOLD;
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
}
