package gov.nysenate.ess.time.util;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import static gov.nysenate.ess.time.model.EssTimeConstants.ACCRUAL_INCREMENT;
import static gov.nysenate.ess.time.model.EssTimeConstants.MAX_YTD_HOURS;

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

    public static BigDecimal roundAccrualValue(BigDecimal value) {
        BigDecimal accrualMultiplier = BigDecimal.ONE.divide(ACCRUAL_INCREMENT, RoundingMode.HALF_UP);
        return value.multiply(accrualMultiplier)
                .setScale(0, RoundingMode.CEILING)
                .divide(accrualMultiplier);
    }
}
