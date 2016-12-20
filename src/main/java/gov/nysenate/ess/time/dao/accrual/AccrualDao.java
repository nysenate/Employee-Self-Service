package gov.nysenate.ess.time.dao.accrual;

import com.google.common.collect.Range;
import gov.nysenate.ess.core.util.LimitOffset;
import gov.nysenate.ess.core.util.SortOrder;
import gov.nysenate.ess.core.dao.base.BaseDao;
import gov.nysenate.ess.time.model.accrual.AnnualAccSummary;
import gov.nysenate.ess.core.model.period.PayPeriod;
import gov.nysenate.ess.time.model.accrual.PeriodAccSummary;
import gov.nysenate.ess.time.model.accrual.PeriodAccUsage;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.TreeMap;

/**
 * Data access layer for retrieving and computing accrual information
 * (e.g personal hours, vacation hours, etc).
 */
public interface AccrualDao extends BaseDao
{
    /**
     * Retrieve the per-pay-period accrual summaries for the given employee that occur before a specific date.
     * A TreeMap is returned which maps the PeriodAccSummary object with it's associated 'basePayPeriod'.
     *
     * @param empId int - Employee id
     * @param endDate LocalDate - The retrieved period summaries will be effective prior to this date.
     * @param limOff LimitOffset - Limit the result set
     * @param order SortOrder - Order by pay period end date
     * @return TreeMap<LocalDate, PeriodAccSummary>
     */
    TreeMap<PayPeriod, PeriodAccSummary> getPeriodAccruals(int empId, LocalDate endDate, LimitOffset limOff,
                                                           SortOrder order);

    /**
     * Retrieve the running annual accrual summaries for the given employee for all years before or on the 'endYear'.
     *
     * @param empId int - Employee id
     * @param endYear int - The year to retrieve annual summaries until.
     * @return TreeMap<Integer, AnnualAccSummary>
     */
    TreeMap<Integer, AnnualAccSummary> getAnnualAccruals(int empId, int endYear);

    /**
     * Get a list of annual accrual summaries that have been updated since the given datetime
     * @param updatedSince LocalDateTime - will retrieve summaries updated after this time
     * @return List<AnnualAccSummary>
     */
    List<AnnualAccSummary> getAnnualAccsUpdatedSince(LocalDateTime updatedSince);

    /**
     * Retrieve the period accrual usage objects that represent the hours charged during a given pay period.
     *
     * @param empId int - Employee id
     * @param dateRange Range<LocalDate> - The date range to obtain usages within
     * @return TreeMap<PayPeriod, PeriodAccUsage>
     */
    TreeMap<PayPeriod, PeriodAccUsage> getPeriodAccrualUsages(int empId, Range<LocalDate> dateRange);

}