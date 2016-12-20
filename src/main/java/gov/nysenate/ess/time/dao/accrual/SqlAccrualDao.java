package gov.nysenate.ess.time.dao.accrual;

import com.google.common.collect.Maps;
import com.google.common.collect.Range;
import gov.nysenate.ess.core.util.DateUtils;
import gov.nysenate.ess.core.util.LimitOffset;
import gov.nysenate.ess.core.util.OrderBy;
import gov.nysenate.ess.core.util.SortOrder;
import gov.nysenate.ess.time.dao.accrual.mapper.AnnualAccSummaryRowMapper;
import gov.nysenate.ess.time.dao.accrual.mapper.PeriodAccUsageRowMapper;
import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import gov.nysenate.ess.time.model.accrual.AnnualAccSummary;
import gov.nysenate.ess.time.model.accrual.PeriodAccUsage;
import gov.nysenate.ess.core.model.period.PayPeriod;
import gov.nysenate.ess.time.dao.accrual.mapper.PeriodAccSummaryRowMapper;
import gov.nysenate.ess.time.model.accrual.PeriodAccSummary;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.TreeMap;

/** {@inheritDoc}
 *
 *  The accrual data is stored primarily in three database tables in SFMS:
 *  PM23ATTEND - Stores annual rolling counts of accrued/used hours as well as the number of pay periods worked.
 *  PD23ACCUSAGE - Stores counts of accrued/used hours on a pay period basis as well as the expected YTD hours to work.
 *  PD23ATTEND - Stores usage hours on a pay period basis (updated more frequently).
 *
 *  PM23ATTEND and PD23ACCUSAGE are refreshed with a substantial delay from the end of the prior pay period such
 *  that it gives employees enough time to submit old timesheets without having to recompute data constantly. However
 *  the side effect is that there will likely not be accrual information for the more recent pay periods. The
 *  accruals will have to be computed to account for the missing data via the service layer.
 */
@Service("sqlAccrual")
public class SqlAccrualDao extends SqlBaseDao implements AccrualDao
{
    /** --- Implemented Methods --- */

    /** {@inheritDoc} */
    @Override
    public TreeMap<PayPeriod, PeriodAccSummary> getPeriodAccruals(int empId, LocalDate beforeDate,
                                                                  LimitOffset limOff, SortOrder order) {
        MapSqlParameterSource params = getPeriodAccSummaryParams(empId, beforeDate);
        OrderBy orderBy = new OrderBy("DTEND", order);
        List<PeriodAccSummary> periodAccSummaries =
            remoteNamedJdbc.query(SqlAccrualQuery.GET_PERIOD_ACC_SUMMARIES.getSql(schemaMap(), orderBy, limOff), params, new PeriodAccSummaryRowMapper("",""));
        return new TreeMap<>(Maps.uniqueIndex(periodAccSummaries, PeriodAccSummary::getRefPayPeriod));
    }

    /** {@inheritDoc} */
    @Override
    public TreeMap<Integer, AnnualAccSummary> getAnnualAccruals(int empId, int endYear) {
        MapSqlParameterSource params = getAnnualAccSummaryParams(empId, endYear);
        List<AnnualAccSummary> annualAccRecs =
            remoteNamedJdbc.query(SqlAccrualQuery.GET_ANNUAL_ACC_SUMMARIES_BY_EMP.getSql(schemaMap()), params, new AnnualAccSummaryRowMapper());
        return new TreeMap<>(Maps.uniqueIndex(annualAccRecs, AnnualAccSummary::getYear));
    }

    /** {@inheritDoc} */
    @Override
    public List<AnnualAccSummary> getAnnualAccsUpdatedSince(LocalDateTime updatedSince) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("updateDateTime", toDate(updatedSince));
        return remoteNamedJdbc.query(SqlAccrualQuery.GET_ANNUAL_ACC_SUMMARIES_UPDATED_SINCE.getSql(schemaMap()),
                params, new AnnualAccSummaryRowMapper());
    }

    /** {@inheritDoc} */
    @Override
    public TreeMap<PayPeriod, PeriodAccUsage> getPeriodAccrualUsages(int empId, Range<LocalDate> dateRange) {
        MapSqlParameterSource params = getAccrualUsageParams(empId, DateUtils.startOfDateRange(dateRange),
                                                                    DateUtils.endOfDateRange(dateRange));
        List<PeriodAccUsage> usageRecs =
            remoteNamedJdbc.query(SqlAccrualQuery.GET_PERIOD_ACCRUAL_USAGE.getSql(schemaMap()), params, new PeriodAccUsageRowMapper("",""));
        return new TreeMap<>(Maps.uniqueIndex(usageRecs, PeriodAccUsage::getPayPeriod));
    }

    /** --- Param Source Methods --- */

    protected MapSqlParameterSource getAnnualAccSummaryParams(int empId, int endYear) {
        return new MapSqlParameterSource()
            .addValue("empId", empId)
            .addValue("endYear", endYear);
    }

    protected MapSqlParameterSource getPeriodAccSummaryParams(int empId, LocalDate endDate) {
        return new MapSqlParameterSource()
            .addValue("empId", empId)
            .addValue("beforeDate", DateUtils.toDate(endDate));
    }

    protected MapSqlParameterSource getAccrualUsageParams(int empId, LocalDate start, LocalDate end) {
        return new MapSqlParameterSource()
            .addValue("empId", empId)
            .addValue("startDate", DateUtils.toDate(start))
            .addValue("endDate", DateUtils.toDate(end));
    }
}