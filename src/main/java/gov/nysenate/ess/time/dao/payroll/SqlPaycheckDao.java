package gov.nysenate.ess.time.dao.payroll;

import com.google.common.collect.Range;
import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import gov.nysenate.ess.core.util.DateUtils;
import gov.nysenate.ess.core.util.OrderBy;
import gov.nysenate.ess.core.util.SortOrder;
import gov.nysenate.ess.time.dao.payroll.mapper.PaycheckHandler;
import gov.nysenate.ess.time.model.payroll.Paycheck;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public class SqlPaycheckDao extends SqlBaseDao implements PayCheckDao
{
    private static final OrderBy defaultOrderBy = new OrderBy("DTCHECK", SortOrder.ASC);

    /** {@inheritDoc} */
    @Override
    public List<Paycheck> getEmployeePaychecksForYear(int empId, int year) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("empId", empId)
              .addValue("year", year);
        return getPaycheckListForQuery(SqlPaycheckQuery.GET_EMPLOYEE_PAYCHECKS_BY_YEAR, params);
    }

    /** {@inheritDoc} */
    @Override
    public List<Paycheck> getEmployeePaychecksForDates(int empId, Range<LocalDate> dateRange) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("empId", empId);
        params.addValue("beginDate", toDate(DateUtils.startOfDateRange(dateRange)));
        params.addValue("endDate", toDate(DateUtils.endOfDateRange(dateRange)));
        return getPaycheckListForQuery(SqlPaycheckQuery.GET_EMPLOYEE_PAYCHECKS_BY_DATE, params);
    }

    /** --- Internal Methods --- */

    private List<Paycheck> getPaycheckListForQuery(SqlPaycheckQuery query, SqlParameterSource params) {
        final String sql = query.getSql(schemaMap(), defaultOrderBy);
        PaycheckHandler handler = new PaycheckHandler();
        remoteNamedJdbc.query(sql, params, handler);
        return handler.getPaychecks();
    }
}
