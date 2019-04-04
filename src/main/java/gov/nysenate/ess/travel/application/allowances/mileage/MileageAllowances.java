package gov.nysenate.ess.travel.application.allowances.mileage;

import com.google.common.collect.ImmutableSortedSet;
import gov.nysenate.ess.travel.application.route.Leg;
import gov.nysenate.ess.travel.utils.Dollars;

import java.util.Collection;
import java.util.Comparator;

public class MileageAllowances {

    private static final double MILE_THRESHOLD = 35.0;
    private static final Comparator<Leg> dateComparator = Comparator.comparing(Leg::travelDate);
    private final ImmutableSortedSet<Leg> mileagePerDiems;

    public MileageAllowances(Collection<Leg> legs) {
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
    public Dollars maximumAllowance() {
        if (tripQualifiesForReimbursement()) {
            return allLegs().stream()
                    .map(Leg::maximumAllowance)
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
    public Dollars requestedAllowance() {
        if (tripQualifiesForReimbursement()) {
            return requestedLegs().stream()
                    .map(Leg::requestedAllowance)
                    .reduce(Dollars.ZERO, Dollars::add);
        } else {
            return Dollars.ZERO;
        }
    }

    public ImmutableSortedSet<Leg> allLegs() {
        return mileagePerDiems;
    }

    public ImmutableSortedSet<Leg> requestedLegs() {
        return mileagePerDiems.stream()
                .filter(Leg::isReimbursementRequested)
                .collect(ImmutableSortedSet.toImmutableSortedSet(dateComparator));
    }

    private ImmutableSortedSet<Leg> outboundLegs() {
        return allLegs().stream()
                .filter(Leg::isOutbound)
                .collect(ImmutableSortedSet.toImmutableSortedSet(dateComparator));
    }

    private boolean tripQualifiesForReimbursement() {
        double outboundMiles = outboundLegs().stream()
                .mapToDouble(Leg::miles)
                .sum();
        return outboundMiles > MILE_THRESHOLD;
    }
}
