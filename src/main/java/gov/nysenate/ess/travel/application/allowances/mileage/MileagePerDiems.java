package gov.nysenate.ess.travel.application.allowances.mileage;

import com.google.common.collect.ImmutableList;
import gov.nysenate.ess.travel.application.route.Leg;
import gov.nysenate.ess.travel.utils.Dollars;

import java.util.Collection;

public class MileagePerDiems {

    private static final double MILE_THRESHOLD = 35.0;
    private final ImmutableList<Leg> mileagePerDiems;

    public MileagePerDiems(Collection<Leg> legs) {
        this.mileagePerDiems = ImmutableList.copyOf(legs);
    }

    /**
     * The maximum mileage allowance allowed for this trip.
     *
     * @return
     */
    public Dollars maximumPerDiem() {
        if (tripQualifiesForReimbursement()) {
            return allLegs().stream()
                    .map(Leg::maximumPerDiem)
                    .reduce(Dollars.ZERO, Dollars::add);
        } else {
            return Dollars.ZERO;
        }
    }

    /**
     * The requested mileage allowance.
     * <p>
     * Outbound mileage must be > 35 miles to qualify for any mileage allowance.
     * Must be traveling via personal auto for a leg to qualify.
     *
     * @return
     */
    public Dollars requestedPerDiem() {
        if (tripQualifiesForReimbursement()) {
            return requestedLegs().stream()
                    .map(Leg::requestedPerDiem)
                    .reduce(Dollars.ZERO, Dollars::add);
        } else {
            return Dollars.ZERO;
        }
    }

    public ImmutableList<Leg> allLegs() {
        return mileagePerDiems;
    }

    /**
     * Legs that are allowed to be reimbursed for travel.
     */
    public ImmutableList<Leg> qualifyingLegs() {
        return mileagePerDiems.stream()
                .filter(Leg::qualifiesForMileageReimbursement)
                .collect(ImmutableList.toImmutableList());
    }

    /**
     * Legs that are allowed to be reimbursed for travel and the travel
     * has requested reimbursement.
     */
    public ImmutableList<Leg> requestedLegs() {
        return qualifyingLegs().stream()
                .filter(Leg::isReimbursementRequested)
                .collect(ImmutableList.toImmutableList());
    }

    public boolean tripQualifiesForReimbursement() {
        double outboundMiles = qualifyingLegs().stream()
                .mapToDouble(Leg::miles)
                .sum();
        return outboundMiles > MILE_THRESHOLD;
    }

    private ImmutableList<Leg> outboundLegs() {
        return allLegs().stream()
                .filter(Leg::isOutbound)
                .collect(ImmutableList.toImmutableList());
    }
}
