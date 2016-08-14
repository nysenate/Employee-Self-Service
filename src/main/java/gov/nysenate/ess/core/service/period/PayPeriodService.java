package gov.nysenate.ess.core.service.period;

import com.google.common.collect.Range;
import gov.nysenate.ess.core.model.period.PayPeriod;
import gov.nysenate.ess.core.model.period.PayPeriodType;
import gov.nysenate.ess.core.util.SortOrder;

import java.time.LocalDate;
import java.util.List;

public interface PayPeriodService
{
    PayPeriod getPayPeriod(PayPeriodType type, LocalDate date);

    List<PayPeriod> getPayPeriods(PayPeriodType type, Range<LocalDate> dateRange, SortOrder dateOrder);
}
