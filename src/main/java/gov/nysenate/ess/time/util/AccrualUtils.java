package gov.nysenate.ess.time.util;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import static gov.nysenate.ess.time.model.EssTimeConstants.*;

/**
 * Utility methods for accruals and expected hours
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
     * Get the number of hours an employee is expected to work in a pay period,
     * given the number of hours expected for a whole year.
     *
     * @param minTotalHours BigDecimal
     * @return BigDecimal
     */
    public static BigDecimal getHoursPerPeriod(BigDecimal minTotalHours) {
        BigDecimal rawHoursPerDay = minTotalHours.divide(MAX_DAYS_PER_YEAR, FOUR_DIGITS_MAX);
        BigDecimal rawHoursPerPeriod = rawHoursPerDay.multiply(BigDecimal.TEN);
        return roundExpectedHours(rawHoursPerPeriod);
    }

    /**
     * Gets the number of work hours expected for a single day,
     * given the number of total hours expected for a year.
     * This value is not rounded.
     *
     * @param minTotalHours BigDecimal
     * @return BigDecimal
     */
    public static BigDecimal getHoursPerDay(BigDecimal minTotalHours) {
        BigDecimal hoursPerPeriod = getHoursPerPeriod(minTotalHours);
        return hoursPerPeriod.divide(BigDecimal.TEN, FOUR_DIGITS_MAX);
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
        value = value.setScale(4, RoundingMode.FLOOR);
        BigDecimal multiplier = BigDecimal.ONE.divide(increment, RoundingMode.HALF_UP);
        multiplier = multiplier.setScale(4, RoundingMode.FLOOR);
        /// Below previously was:   .setScale(0, RoundingMode.CEILING)
        /// Issue was that accruals were not rounding correctly.  EX: Employee with Sick Accrual 2.250015 rounded to
        /// 2.5 with CEILING when it should have been 2.25..
        /// Sick, Vac can accrue in .25 increments but have to be used in .5 increments
        /// Personal, Work Time are only in .5 increments.

        return value.multiply(multiplier)
                .setScale(0, RoundingMode.CEILING)
                .divide(multiplier);
    }
}
