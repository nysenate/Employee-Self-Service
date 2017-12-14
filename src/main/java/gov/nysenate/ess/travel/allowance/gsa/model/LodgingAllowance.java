package gov.nysenate.ess.travel.allowance.gsa.model;

import java.math.BigDecimal;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public class LodgingAllowance {

    private BigDecimal lodging;

    // Map<TravelDestionation, Map<NightOfStay, allowance>> blehh

    public LodgingAllowance(BigDecimal lodging) {
        checkArgument(checkNotNull(lodging).signum() >= 0);
        this.lodging = lodging;
    }
}
