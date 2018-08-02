package gov.nysenate.ess.travel.application.allowances.lodging;

import com.google.common.collect.ImmutableList;
import gov.nysenate.ess.travel.utils.Dollars;

import java.util.List;
import java.util.Objects;

public class LodgingAllowances {

    private final ImmutableList<LodgingAllowance> lodgingAllowances;

    public LodgingAllowances(List<LodgingAllowance> lodgingAllowances) {
        this.lodgingAllowances = ImmutableList.copyOf(lodgingAllowances);
    }

    public Dollars totalAllowance() {
        return getLodgingAllowances().stream()
                .map(LodgingAllowance::allowance)
                .reduce(Dollars.ZERO, Dollars::add);
    }

    protected ImmutableList<LodgingAllowance> getLodgingAllowances() {
        return lodgingAllowances;
    }

    @Override
    public String toString() {
        return "LodgingAllowances{" +
                "lodgingAllowances=" + lodgingAllowances +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LodgingAllowances that = (LodgingAllowances) o;
        return Objects.equals(lodgingAllowances, that.lodgingAllowances);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lodgingAllowances);
    }
}
