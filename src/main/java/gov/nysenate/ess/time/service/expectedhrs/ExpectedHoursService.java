package gov.nysenate.ess.time.service.expectedhrs;

import gov.nysenate.ess.core.model.period.PayPeriod;

import java.math.BigDecimal;

/**
 * @author Brian Heitner
 *
 */
public interface ExpectedHoursService {

    public BigDecimal getExpectedHours(PayPeriod payPeriod, int empId);

}
