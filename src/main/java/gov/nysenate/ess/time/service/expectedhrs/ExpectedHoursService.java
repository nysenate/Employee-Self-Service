package gov.nysenate.ess.time.service.expectedhrs;

import com.google.common.collect.Range;
import gov.nysenate.ess.core.model.period.PayPeriod;
import gov.nysenate.ess.time.model.expectedhrs.ExpectedHours;
import gov.nysenate.ess.time.model.expectedhrs.InvalidExpectedHourDatesEx;
import gov.nysenate.ess.time.service.allowance.AllowanceRecordCache;

import java.time.LocalDate;

/**
 * @author Brian Heitner
 * @author Sam Stouffer
 *
 * Defines a service that can provide information about an employee's expected hours.
 */
public interface ExpectedHoursService {

    /**
     * Calculate {@link ExpectedHours} for the given date range.
     *
     * @param empId int
     * @param dateRange Range<LocalDate>
     * @return {@link ExpectedHours}
     * @throws InvalidExpectedHourDatesEx if provided date range is invalid
     */
    ExpectedHours getExpectedHours(int empId, Range<LocalDate> dateRange) throws InvalidExpectedHourDatesEx;


    /**
     * Calculate {@link ExpectedHours} for the given pay period
     *
     * @param empId int
     * @param payPeriod {@link PayPeriod}
     * @return {@link ExpectedHours}
     * @throws InvalidExpectedHourDatesEx if provided date range is invalid
     */
    default ExpectedHours getExpectedHours(int empId, PayPeriod payPeriod) throws InvalidExpectedHourDatesEx {
        return getExpectedHours(empId, payPeriod.getDateRange());
    }

    /**
     * Calculate {@link ExpectedHours} for the given date range, reusing already loaded records.
     *
     * <p>Equivalent to {@link #getExpectedHours(int, Range)} except that the allowance lookup it
     * performs draws its records from the supplied cache. Callers asking about many ranges within
     * the same year should share one cache across those calls.
     *
     * @param empId int
     * @param dateRange Range<LocalDate>
     * @param recordCache {@link AllowanceRecordCache} shared across a single computation
     * @return {@link ExpectedHours}
     * @throws InvalidExpectedHourDatesEx if provided date range is invalid
     */
    ExpectedHours getExpectedHours(int empId, Range<LocalDate> dateRange, AllowanceRecordCache recordCache)
            throws InvalidExpectedHourDatesEx;

    /** Pay period form of {@link #getExpectedHours(int, Range, AllowanceRecordCache)}. */
    default ExpectedHours getExpectedHours(int empId, PayPeriod payPeriod, AllowanceRecordCache recordCache)
            throws InvalidExpectedHourDatesEx {
        return getExpectedHours(empId, payPeriod.getDateRange(), recordCache);
    }
}
