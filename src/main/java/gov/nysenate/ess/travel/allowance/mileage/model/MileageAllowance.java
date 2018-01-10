package gov.nysenate.ess.travel.allowance.mileage.model;


import com.google.common.collect.ImmutableMap;

public class MileageAllowance {

    private final ImmutableMap<Leg, Long> outboundLegDistances;
    private final ImmutableMap<Leg, Long> returnLegDistances;

    public MileageAllowance() {
        outboundLegDistances = ImmutableMap.of();
        returnLegDistances = ImmutableMap.of();
    }

    public MileageAllowance(ImmutableMap<Leg, Long> outboundLegDistances,
                            ImmutableMap<Leg, Long> returnLegDistances) {
        this.outboundLegDistances = outboundLegDistances;
        this.returnLegDistances = returnLegDistances;
    }

    public MileageAllowance addOutboundLeg(Leg leg, Long distance) {
        return new MileageAllowance(copyToBuilder(getOutboundLegDistances()).put(leg, distance).build(),
                getReturnLegDistances());
    }

    public MileageAllowance addReturnLeg(Leg leg, Long distance) {
        return new MileageAllowance(getOutboundLegDistances(),
                copyToBuilder(getReturnLegDistances()).put(leg, distance).build());
    }

    public ImmutableMap<Leg, Long> getOutboundLegDistances() {
        return outboundLegDistances;
    }

    public ImmutableMap<Leg, Long> getReturnLegDistances() {
        return returnLegDistances;
    }

    private ImmutableMap.Builder<Leg, Long> copyToBuilder(ImmutableMap<Leg, Long> map) {
        return ImmutableMap.<Leg, Long>builder().putAll(map);
    }
}
