package gov.nysenate.ess.travel.application.allowances.mileage;

import com.google.common.collect.ImmutableSortedSet;
import gov.nysenate.ess.travel.application.route.Leg;
import gov.nysenate.ess.travel.utils.Dollars;

import java.util.Collection;
import java.util.Comparator;

public class MileagePerDiems {

    private static final double MILE_THRESHOLD = 35.0;
    private static final Comparator<Leg> dateComparator = Comparator.comparing(Leg::travelDate);
    private final ImmutableSortedSet<Leg> mileagePerDiems;

    public MileagePerDiems(Collection<Leg> legs) {
        this.mileagePerDiems = ImmutableSortedSet
                .orderedBy(dateComparator)
                .addAll(legs)
                .build();
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

    public ImmutableSortedSet<Leg> allLegs() {
        return mileagePerDiems;
    }

    /**
     * Legs that are allowed to be reimbursed for travel.
     */
    public ImmutableSortedSet<Leg> qualifyingLegs() {
        return mileagePerDiems.stream()
                .filter(Leg::qualifiesForMileageReimbursement)
                .collect(ImmutableSortedSet.toImmutableSortedSet(dateComparator));
    }

    /**
     * Legs that are allowed to be reimbursed for travel and the travel
     * has requested reimbursement.
     */
    public ImmutableSortedSet<Leg> requestedLegs() {
        return qualifyingLegs().stream()
                .filter(Leg::isReimbursementRequested)
                .collect(ImmutableSortedSet.toImmutableSortedSet(dateComparator));
    }

    private ImmutableSortedSet<Leg> outboundLegs() {
        return allLegs().stream()
                .filter(Leg::isOutbound)
                .collect(ImmutableSortedSet.toImmutableSortedSet(dateComparator));
    }

    private boolean tripQualifiesForReimbursement() {
        double outboundMiles = qualifyingLegs().stream()
                .mapToDouble(Leg::miles)
                .sum();
        return outboundMiles > MILE_THRESHOLD;
    }
}
