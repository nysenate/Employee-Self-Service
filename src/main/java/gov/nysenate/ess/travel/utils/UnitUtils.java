package gov.nysenate.ess.travel.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class UnitUtils {

    private static final BigDecimal METERS_PER_MILE = new BigDecimal("1609.344");

    /**
     * Converts meters to miles rounding to the nearest tenth of a mile.
     * @param meters meters to convert to miles, must be a positive number.
     */
    public static BigDecimal metersToMiles(long meters) {
        if (meters < 0L) {
            throw new IllegalArgumentException("Cannot convert negative meters to miles.");
        }
        // Round to tenth. Rounded required when dividing.
        return BigDecimal.valueOf(meters).divide(METERS_PER_MILE, 1, RoundingMode.HALF_UP);
    }
}
