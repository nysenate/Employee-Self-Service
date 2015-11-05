package gov.nysenate.ess.core.dao.period;

import com.google.common.collect.Range;
import gov.nysenate.ess.core.dao.base.BaseDao;
import gov.nysenate.ess.core.model.period.PayPeriod;
import gov.nysenate.ess.core.model.period.PayPeriodException;
import gov.nysenate.ess.core.model.period.PayPeriodType;
import gov.nysenate.ess.core.util.SortOrder;

import java.time.LocalDate;
import java.util.List;

/**
 * Data access layer for providing basic pay period information.
 */
public interface PayPeriodDao extends BaseDao
{
    /**
     * Retrieve the pay period of the given type that overlaps on the given 'date'.
     *
     * @param type PayPeriodType - The type of pay period.
     * @param date LocalDate - Date to fetch an overlapping pay period for
     * @return PayPeriod
     * @throws PayPeriodException - PayPeriodNotFoundEx if no matches were found.
     */
    public PayPeriod getPayPeriod(PayPeriodType type, LocalDate date) throws PayPeriodException;

    /**
     * Retrieves the pay periods of the given type within the given dateRange. The returned pay periods
     * will either be contained within or overlap with the dateRange.
     *
     * @param type PayPeriodType - The type of pay period.
     * @param dateRange Range<LocalDate> - The start and end date range.
     * @param dateOrder SortOrder - Order by the start date of the pay period
     * @return List<PayPeriod>
     */
    public List<PayPeriod> getPayPeriods(PayPeriodType type, Range<LocalDate> dateRange, SortOrder dateOrder);
}