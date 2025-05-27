package gov.nysenate.ess.travel.request.allowances.mileage;

import com.google.common.collect.ImmutableList;
import gov.nysenate.ess.travel.utils.Dollars;
import gov.nysenate.ess.travel.utils.UnitUtils;

import java.util.Collection;

public class MileagePerDiems {

    /**
     * The minimum outbound mileage needed to qualify for mileage reimbursement.
     */
    private static final double MILE_THRESHOLD = 35.0;
    private final ImmutableList<MileagePerDiem> perDiems;
    private Dollars overrideRate;

    public MileagePerDiems(Collection<MileagePerDiem> perDiems) {
        this(ImmutableList.copyOf(perDiems), Dollars.ZERO);
    }

    public MileagePerDiems(Collection<MileagePerDiem> perDiems, Dollars overrideRate) {
        this.perDiems = ImmutableList.copyOf(perDiems);
        this.overrideRate = overrideRate == null ? Dollars.ZERO : overrideRate;
    }

    /**
     * The total mileage allowance this trip is qualified for and requested.
     * <p>
     * Outbound mileage must be > 35 miles to qualify for any mileage allowance.
     * Must be traveling via personal auto to qualify.
     *
     * @return
     */
    public Dollars totalPerDiemValue() {
        if (tripQualifiesForReimbursement()) {
            return requestedPerDiems().stream()
                    .map(MileagePerDiem::requestedPerDiemValue)
                    .reduce(Dollars.ZERO, Dollars::add);
        } else {
            return Dollars.ZERO;
        }
    }

    public ImmutableList<MileagePerDiem> allPerDiems() {
        return perDiems;
    }

    /**
     * MileagePerDiem's that qualify for reimbursement.
     */
    public ImmutableList<MileagePerDiem> qualifyingPerDiems() {
        return perDiems.stream()
                .filter(MileagePerDiem::qualifiesForReimbursement)
                .collect(ImmutableList.toImmutableList());
    }

    /**
     * MileagePerDiem's that qualify for reimbursement and are requested by the traveler.
     */
    public ImmutableList<MileagePerDiem> requestedPerDiems() {
        return qualifyingPerDiems().stream()
                .filter(MileagePerDiem::isReimbursementRequested)
                .collect(ImmutableList.toImmutableList());
    }

    /**
     * Total mileage of qualifying mileagePerDiem's rounded to the tenth of a mile.
     *
     * @return
     */
    public double totalMileage() {
        return UnitUtils.round(qualifyingPerDiems().stream()
                .mapToDouble(MileagePerDiem::getMiles)
                .sum(), 1);
    }

    /**
     * A trip is eligible for mileage reimbursement if a isMileageReimbursable MethodOfTravel is used
     * on any leg of the trip and the total outbound miles is greater than MILE_THRESHOLD.
     *
     * @return
     */
    public boolean tripQualifiesForReimbursement() {
        return isOutboundMileageGreaterThanTheshold() && isReimbursableMethodOfTravelUsed();
    }

    private boolean isOutboundMileageGreaterThanTheshold() {
        double outboundMiles = perDiems.stream()
                .filter(MileagePerDiem::isOutbound)
                .mapToDouble(MileagePerDiem::getMiles)
                .sum();
        return outboundMiles > MILE_THRESHOLD;
    }

    private boolean isReimbursableMethodOfTravelUsed() {
        return perDiems.stream().anyMatch(MileagePerDiem::qualifiesForReimbursement);
    }

    public boolean isOverridden() {
        return !Dollars.ZERO.equals(getOverrideRate());
    }

    public Dollars getOverrideRate() {
        return overrideRate;
    }
}
