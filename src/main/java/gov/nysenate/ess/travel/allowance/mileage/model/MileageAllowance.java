package gov.nysenate.ess.travel.allowance.mileage.model;

import com.google.common.collect.ImmutableSet;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.maps.internal.ratelimiter.Preconditions.checkNotNull;

/**
 * Contains all info used to calculate the mileage allowance.
 * This includes the irs rate, outbound, and return legs of the trip.
 * To be eligible for mileage reimbursement the outbound portion of the trip
 * must be >= 35 miles.
 *
 * Applies the irs rate for the trip start date to the entire trip.
 * This means trips spanning periods where the irs rate changes will be slightly imprecise.
 * This is acceptable since this value is just an estimate.
 *
 * Leg distances are individually rounded to the nearest tenth of a mile.
 */
public class MileageAllowance {

    /** The outbound driving distance in miles that must be met to be reimbursed for mileage. */
    private static final BigDecimal REIMBURSEMENT_THRESHOLD = new BigDecimal("35.0");

    /**The IRS Rate valid at this travel app start date */
    private final BigDecimal rate;
    private final ImmutableSet<ReimbursableLeg> outboundLegs;
    private final ImmutableSet<ReimbursableLeg> returnLegs;

    public MileageAllowance(BigDecimal rate) {
        this(rate, ImmutableSet.of(), ImmutableSet.of());
    }

    public MileageAllowance(BigDecimal rate,
                            ImmutableSet<ReimbursableLeg> outboundLegs,
                            ImmutableSet<ReimbursableLeg> returnLegs) {
        checkArgument(checkNotNull(rate).signum() >= 0);
        this.rate = rate;
        this.outboundLegs = outboundLegs;
        this.returnLegs = returnLegs;
    }

    /**
     * Returns the estimated allowance for this MileageAllowance.
     * @return
     */
    public BigDecimal getAllowance() {
        if (outboundDistanceBelowThreshold()) {
            return new BigDecimal("0.00");
        }
        return totalDistance().multiply(getRate()).setScale(2, RoundingMode.HALF_UP);
    }

    private boolean outboundDistanceBelowThreshold() {
        return getLegsDistance(getOutboundLegs()).compareTo(REIMBURSEMENT_THRESHOLD) == -1;
    }

    private BigDecimal getLegsDistance(ImmutableSet<ReimbursableLeg> legs) {
        return legs.stream()
                .map(ReimbursableLeg::getDistance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal totalDistance() {
        return getLegsDistance(getOutboundLegs()).add(getLegsDistance(getReturnLegs()));
    }

    /**
     * @param leg
     * @return A new MileageAllowance with the given leg added to the set of outbound legs.
     */
    public MileageAllowance addOutboundLeg(ReimbursableLeg leg) {
        return new MileageAllowance(
                getRate(),
                ImmutableSet.<ReimbursableLeg>builder().addAll(getOutboundLegs()).add(leg).build(),
                getReturnLegs());
    }

    /**
     * @param leg
     * @return A new MileageAllowance with the given leg added to the set of return legs.
     */
    public MileageAllowance addReturnLeg(ReimbursableLeg leg) {
        return new MileageAllowance(
                getRate(),
                getOutboundLegs(),
                ImmutableSet.<ReimbursableLeg>builder().addAll(getReturnLegs()).add(leg).build());
    }

    protected BigDecimal getRate() {
        return rate;
    }

    protected ImmutableSet<ReimbursableLeg> getOutboundLegs() {
        return outboundLegs;
    }

    protected ImmutableSet<ReimbursableLeg> getReturnLegs() {
        return returnLegs;
    }

    @Override
    public String toString() {
        return "MileageAllowance{" +
                "rate=" + rate +
                ", outboundLegs=" + outboundLegs +
                ", returnLegs=" + returnLegs +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MileageAllowance allowance = (MileageAllowance) o;
        return Objects.equals(rate, allowance.rate) &&
                Objects.equals(outboundLegs, allowance.outboundLegs) &&
                Objects.equals(returnLegs, allowance.returnLegs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rate, outboundLegs, returnLegs);
    }
}
