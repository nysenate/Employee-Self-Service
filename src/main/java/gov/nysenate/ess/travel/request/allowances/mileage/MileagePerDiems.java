package gov.nysenate.ess.travel.request.allowances.mileage;

import com.google.common.collect.ImmutableList;
import gov.nysenate.ess.travel.request.route.Leg;
import gov.nysenate.ess.travel.utils.Dollars;
import gov.nysenate.ess.travel.utils.UnitUtils;

import java.util.Collection;

public class MileagePerDiems {

    /**
     * The minimum outbound mileage needed to qualify for mileage reimbursement.
     */
    private static final double MILE_THRESHOLD = 35.0;
    private final ImmutableList<Leg> legs;

    public MileagePerDiems(Collection<Leg> legs) {
        this.legs = ImmutableList.copyOf(legs);
    }

    /**
     * The mileage allowance this trip is qualified for and requested.
     * <p>
     * Outbound mileage must be > 35 miles to qualify for any mileage allowance.
     * Must be traveling via personal auto for a leg to qualify.
     * @return
     */
    public Dollars totalPerDiem() {
        if (tripQualifiesForReimbursement()) {
            return requestedLegs().stream()
                    .map(Leg::requestedPerDiem)
                    .reduce(Dollars.ZERO, Dollars::add);
        } else {
            return Dollars.ZERO;
        }
    }

    public ImmutableList<Leg> allLegs() {
        return legs;
    }

    /**
     * Legs that are allowed to be reimbursed for travel.
     */
    public ImmutableList<Leg> mileageReimbursableLegs() {
        return legs.stream()
                .filter(Leg::qualifiesForMileageReimbursement)
                .collect(ImmutableList.toImmutableList());
    }

    /**
     * Legs that are allowed to be reimbursed for travel and the travel
     * has requested reimbursement.
     */
    public ImmutableList<Leg> requestedLegs() {
        return mileageReimbursableLegs().stream()
                .filter(Leg::isReimbursementRequested)
                .collect(ImmutableList.toImmutableList());
    }

    /**
     * A trip qualifies for mileage reimbursement if a isMileageReimbursable MethodOfTravel is used
     * on any leg of the trip and the total outbound miles is greater than MILE_THRESHOLD.
     * @return
     */
    public boolean tripQualifiesForReimbursement() {
        return isOutboundMileageGreaterThanTheshold() && isReimbursableMethodOfTravelUsed();
    }

    private boolean isOutboundMileageGreaterThanTheshold() {
        double outboundMiles = mileagePerDiems.stream()
                .filter(Leg::isOutbound)
                .mapToDouble(Leg::miles)
                .sum();
        return outboundMiles > MILE_THRESHOLD;
    }

    private boolean isReimbursableMethodOfTravelUsed() {
        return mileagePerDiems.stream().anyMatch(Leg::qualifiesForMileageReimbursement);
    }

    /**
     * Total mileage rounded to the tenth of a mile.
     * @return
     */
    public double totalMileage() {
        return UnitUtils.round(mileageReimbursableLegs().stream()
                .mapToDouble(Leg::miles)
                .sum(), 1);
    }
}
