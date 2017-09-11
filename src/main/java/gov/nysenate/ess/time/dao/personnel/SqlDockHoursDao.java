package gov.nysenate.ess.time.dao.personnel;

import com.google.common.collect.Range;
import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import gov.nysenate.ess.core.util.DateUtils;
import gov.nysenate.ess.time.dao.personnel.mapper.DockHoursRecordRowMapper;
import gov.nysenate.ess.time.model.personnel.DockHoursRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;


/**
 * @author  Brian Heitner
 *
 * This will handle docked hours for a specified employee and date range.
 *
 **/
@Repository
public class SqlDockHoursDao extends SqlBaseDao implements DockHoursDao
{
    private static final Logger logger = LoggerFactory.getLogger(SqlDockHoursDao.class);

    /** {@inheritDoc} */
    @Override
    public List<DockHoursRecord> getDockHourRecords(Integer empId, Range<LocalDate> dates) {
        MapSqlParameterSource params = new MapSqlParameterSource("empId", empId)
                .addValue("startDate", toDate(DateUtils.startOfDateRange(dates)))
                .addValue("endDate", toDate(DateUtils.endOfDateRange(dates)));
        return remoteNamedJdbc.query(SqlDockHoursQuery.GET_PERIOD_DOCK_HOURS.getSql(
                schemaMap()),
                params, new DockHoursRecordRowMapper());
    }
}
