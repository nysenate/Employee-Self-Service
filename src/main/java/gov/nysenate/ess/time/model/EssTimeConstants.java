package gov.nysenate.ess.time.model;

import java.math.BigDecimal;

/**
 * A collection of constant values that are used throughout ess time
 */
public final class EssTimeConstants {

    /** Number of hours employees are required to work on a single work day */
    public static final BigDecimal HOURS_PER_DAY = new BigDecimal(7);

    /** Number of work days in a year ( may vary from year to year, but is always assumed to be this value) */
    public static final BigDecimal MAX_DAYS_PER_YEAR = new BigDecimal(260);

    /** Total number of hours expected for a regular annual employee in a single year */
    public static final BigDecimal MAX_YTD_HOURS = MAX_DAYS_PER_YEAR.multiply(HOURS_PER_DAY);

    /** Specifies the lowest common denominator for accrual values */
    public static final BigDecimal ACCRUAL_INCREMENT = new BigDecimal("0.5");

    /** The number of personal time hours allotted to a regular annual employee for a single year */
    public static final BigDecimal ANNUAL_PER_HOURS = new BigDecimal(35);

    private EssTimeConstants() {}
}
