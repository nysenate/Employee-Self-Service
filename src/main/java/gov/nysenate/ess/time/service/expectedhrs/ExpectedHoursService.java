package gov.nysenate.ess.time.service.expectedhrs;

import com.google.common.collect.Range;
import gov.nysenate.ess.core.model.period.PayPeriod;
import gov.nysenate.ess.time.model.expectedhrs.ExpectedHours;
import gov.nysenate.ess.time.model.expectedhrs.InvalidExpectedHourDatesEx;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @author Brian Heitner
 * @author Sam Stouffer
 *
 * Defines a service that can provide information about an employee's expected hours.
 */
public interface ExpectedHoursService {

    BigDecimal getExpectedHours(int empId, PayPeriod payPeriod);

    ExpectedHours getExpectedHours(int empId, Range<LocalDate> dateRange) throws InvalidExpectedHourDatesEx;
}
