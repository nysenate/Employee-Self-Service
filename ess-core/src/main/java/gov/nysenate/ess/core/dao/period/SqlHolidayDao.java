package gov.nysenate.ess.core.dao.period;

import com.google.common.collect.Range;
import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import gov.nysenate.ess.core.dao.period.mapper.HolidayRowMapper;
import gov.nysenate.ess.core.model.period.Holiday;
import gov.nysenate.ess.core.util.OrderBy;
import gov.nysenate.ess.core.util.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

import static gov.nysenate.ess.core.dao.period.SqlHolidayQuery.*;
import static gov.nysenate.ess.core.util.DateUtils.endOfDateRange;
import static gov.nysenate.ess.core.util.DateUtils.startOfDateRange;

/** {@inheritDoc} */
@Repository
public class SqlHolidayDao extends SqlBaseDao implements HolidayDao
{
    private static final Logger logger = LoggerFactory.getLogger(SqlHolidayDao.class);

    /** {@inheritDoc} */
    @Override
    public Holiday getHoliday(LocalDate date) throws EmptyResultDataAccessException {
        MapSqlParameterSource params = new MapSqlParameterSource("date", toDate(date));
        return remoteNamedJdbc.queryForObject(GET_HOLIDAY_SQL.getSql(schemaMap()), params, new HolidayRowMapper(""));
    }

    /** {@inheritDoc} */
    @Override
    public List<Holiday> getHolidays(Range<LocalDate> dateRange, SortOrder dateOrder) {
        return getHolidays(dateRange, false, dateOrder);
    }

    /** {@inheritDoc} */
    @Override
    public List<Holiday> getHolidays(Range<LocalDate> dateRange, boolean includeQuestionable, SortOrder dateOrder) {
        MapSqlParameterSource params = new MapSqlParameterSource()
            .addValue("startDate", toDate(startOfDateRange(dateRange)))
            .addValue("endDate", toDate(endOfDateRange(dateRange)));
        OrderBy orderBy = new OrderBy("DTHOLIDAY", dateOrder);
        SqlHolidayQuery holidaySql = (includeQuestionable) ? GET_HOLIDAYS_SQL : GET_NON_QUESTIONABLE_HOLIDAYS_SQL;
        return remoteNamedJdbc.query(holidaySql.getSql(schemaMap(), orderBy), params, new HolidayRowMapper(""));
    }
}