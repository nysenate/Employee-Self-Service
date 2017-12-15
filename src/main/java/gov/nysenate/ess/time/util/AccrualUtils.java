package gov.nysenate.ess.time.util;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import static gov.nysenate.ess.time.model.EssTimeConstants.*;

/**
 * Utility methods for accruals
 */
public final class AccrualUtils {

    private static final MathContext FOUR_DIGITS_MAX = new MathContext(4);

    private AccrualUtils() {}

    /**
     * Get a prorate percentage based on the minimum total hours worked in a year
     * @param minTotalHours BigDecimal
     * @return BigDecimal
     */
    public static BigDecimal getProratePercentage(BigDecimal minTotalHours) {
        if (minTotalHours == null) {
            return BigDecimal.ZERO;
        }
        return minTotalHours.divide(MAX_YTD_HOURS, FOUR_DIGITS_MAX);
    }

    /**
     * Gets the number of work hours expected for a single day,
     * given the number of total hours expected for a year.
     *
     * @param minTotalHours BigDecimal
     * @return BigDecimal
     */
    public static BigDecimal getHoursPerDay(BigDecimal minTotalHours) {
        BigDecimal hoursPerDay = minTotalHours.divide(MAX_DAYS_PER_YEAR, FOUR_DIGITS_MAX);
        return roundExpectedHours(hoursPerDay);
    }

    /**
     * Round a sick or vacation accrual value
     * @param hours BigDecimal - sick/vacation hours or rate
     * @return BigDecimal - rounded value
     */
    public static BigDecimal roundSickVacHours(BigDecimal hours) {
        return roundAccrualValue(hours, SICK_VAC_INCREMENT);
    }

    /**
     * Round a personal accrual value
     * @param hours BigDecimal - sick/vacation hours or rate
     * @return BigDecimal - rounded value
     */
    public static BigDecimal roundPersonalHours(BigDecimal hours) {
        return roundAccrualValue(hours, PER_HOUR_INCREMENT);
    }

    /**
     * Round an expected hours value
     * @param hours BigDecimal - sick/vacation hours or rate
     * @return BigDecimal - rounded value
     */
    public static BigDecimal roundExpectedHours(BigDecimal hours) {
        BigDecimal multiplier = BigDecimal.ONE.divide(EXPECTED_HRS_INCREMENT, RoundingMode.HALF_UP);
        return hours.multiply(multiplier)
                .setScale(0, RoundingMode.HALF_UP)
                .divide(multiplier);
    }

    /* --- Internal Methods --- */

    /**
     * Helper method for rounding various accrual values
     * @param value BigDecimal
     * @param increment BigDecimal
     * @return BigDecimal
     */
    private static BigDecimal roundAccrualValue(BigDecimal value, BigDecimal increment) {
        BigDecimal multiplier = BigDecimal.ONE.divide(increment, RoundingMode.HALF_UP);
        return value.multiply(multiplier)
                .setScale(0, RoundingMode.CEILING)
                .divide(multiplier);
    }
}
