package gov.nysenate.ess.travel.application.allowances.mileage;

import com.google.common.collect.ImmutableList;
import gov.nysenate.ess.travel.utils.Dollars;

import java.util.List;
import java.util.stream.Stream;

public class MileageAllowances {

    private static final double MILEAGE_THRESHOLD = 35.0;

    private final ImmutableList<MileageAllowance> outboundAllowances;
    private final ImmutableList<MileageAllowance> returnAllowances;

    public MileageAllowances(List<MileageAllowance> outboundAllowances, List<MileageAllowance> returnAllowances) {
        this.outboundAllowances = ImmutableList.copyOf(outboundAllowances);
        this.returnAllowances = ImmutableList.copyOf(returnAllowances);
    }

    public Dollars totalAllowance() {
        if (qualifiesForReimbursement()) {
            return Stream.concat(outboundAllowances.stream(), returnAllowances.stream())
                    .map(MileageAllowance::allowance)
                    .reduce(Dollars.ZERO, Dollars::add);
        }
        else {
            return Dollars.ZERO;
        }
    }

    public double totalMiles() {
        return Stream.concat(outboundAllowances.stream(), returnAllowances.stream())
                .mapToDouble(MileageAllowance::getMiles)
                .sum();
    }

    private boolean qualifiesForReimbursement() {
        double outboundMiles = outboundAllowances.stream()
                .mapToDouble(MileageAllowance::getMiles)
                .sum();
            return outboundMiles >= MILEAGE_THRESHOLD;
    }

    protected ImmutableList<MileageAllowance> getOutboundAllowances() {
        return this.outboundAllowances;
    }

    protected ImmutableList<MileageAllowance> getReturnAllowances() {
        return this.returnAllowances;
    }

    @Override
    public String toString() {
        return "MileageAllowances{" +
                "outboundAllowances=" + outboundAllowances +
                ", returnAllowances=" + returnAllowances +
                '}';
    }
}
