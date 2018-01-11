package gov.nysenate.ess.travel.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class UnitUtils {

    /**
     * Converts meters to miles rounding to the nearest tenth of a mile.
     * @param meters
     * @return
     */
    public static BigDecimal metersToMiles(long meters) {
        int SCALE = 1;
        BigDecimal METERS_PER_MILE = new BigDecimal("1609.344");
        BigDecimal miles = BigDecimal.valueOf(meters).divide(METERS_PER_MILE, SCALE, RoundingMode.HALF_UP);
        return miles;
    }
}
