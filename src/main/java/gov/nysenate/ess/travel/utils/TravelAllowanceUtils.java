package gov.nysenate.ess.travel.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class TravelAllowanceUtils {

    /**
     * Rounds a big decimal to 2 digits using the rounding mode for monetary transactions.
     */
    public static BigDecimal round(BigDecimal d) {
        return d.setScale(2, RoundingMode.HALF_UP);
    }
}
