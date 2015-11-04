package gov.nysenate.ess.core.service.period;

import com.google.common.collect.Range;
import gov.nysenate.ess.core.model.period.PayPeriod;
import gov.nysenate.ess.core.model.period.PayPeriodType;
import gov.nysenate.ess.core.util.SortOrder;

import java.time.LocalDate;
import java.util.List;

public interface PayPeriodService
{
    public PayPeriod getPayPeriod(PayPeriodType type, LocalDate date);

    public List<PayPeriod> getPayPeriods(PayPeriodType type, Range<LocalDate> dateRange, SortOrder dateOrder);

    /** Get a list of pay periods that are currently open for the given employee */
    public List<PayPeriod> getOpenPayPeriods(PayPeriodType type, Integer empId, SortOrder dateOrder);
}
