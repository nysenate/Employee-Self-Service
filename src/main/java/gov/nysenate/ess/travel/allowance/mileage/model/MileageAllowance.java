package gov.nysenate.ess.travel.allowance.mileage.model;

import com.google.common.collect.ImmutableSet;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.maps.internal.ratelimiter.Preconditions.checkNotNull;

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

    public BigDecimal getReimbursement() {
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

    public MileageAllowance addOutboundLeg(ReimbursableLeg leg) {
        return new MileageAllowance(
                getRate(),
                ImmutableSet.<ReimbursableLeg>builder().addAll(getOutboundLegs()).add(leg).build(),
                getReturnLegs());
    }

    public MileageAllowance addReturnLeg(ReimbursableLeg leg) {
        return new MileageAllowance(
                getRate(),
                getOutboundLegs(),
                ImmutableSet.<ReimbursableLeg>builder().addAll(getReturnLegs()).add(leg).build());
    }

    public BigDecimal getRate() {
        return rate;
    }

    public ImmutableSet<ReimbursableLeg> getOutboundLegs() {
        return outboundLegs;
    }

    public ImmutableSet<ReimbursableLeg> getReturnLegs() {
        return returnLegs;
    }
}
