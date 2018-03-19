package gov.nysenate.ess.travel.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class UnitUtils {

    /**
     * Converts meters to miles rounding to the nearest tenth of a mile.
     * @param meters meters to convert to miles, must be a positive number.
     */
    public static BigDecimal metersToMiles(long meters) {
        if (meters < 0L) {
            throw new IllegalArgumentException("Cannot convert negative meters to miles.");
        }
        BigDecimal METERS_PER_MILE = new BigDecimal("1609.344");
        // Round to tenth. Rounded required when dividing.
        return BigDecimal.valueOf(meters).divide(METERS_PER_MILE, 1, RoundingMode.HALF_UP);
    }

    /**
     * Rounds a big decimal to 1 digit.
     * e.g. - 1.44 -> 1.4
     *      - 1.45 -> 1.5
     */
    public static BigDecimal roundToTenth(BigDecimal d) {
        return d.setScale(1, RoundingMode.HALF_UP);
    }
}
