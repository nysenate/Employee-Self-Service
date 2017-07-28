package gov.nysenate.ess.time.dao.personnel;

import com.google.common.collect.Range;
import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import gov.nysenate.ess.core.util.DateUtils;
import gov.nysenate.ess.time.model.personnel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;
import  gov.nysenate.ess.time.dao.personnel.mapper.DockHoursRecordRowMapper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;


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

    /**
     * Return the Total Docked Hours within a specified date range. Since docked hours
     * are summed up for each record based on the SQL Query used in getDockHourRecords,
     * we only need to look at the latest record. This record can be earlier than the
     * end date because SFMS may not have processed records up to the end date. Docked
     * Hours will only  be in processed records.
     *
     * @param empId Integer - Employee Id
     * @param dates Range<LocalDate> - Looks for changes after this date
     * @return BigDecimal  - Total Docked Hours
     */

    public BigDecimal getDockHourTotal(Integer empId, Range<LocalDate> dates) {

        Comparator<DockHoursRecord> dockHoursRecordDesc =  (DockHoursRecord o1, DockHoursRecord o2)-> o2.getEndDate().compareTo(o1.getEndDate());

        List<DockHoursRecord> docHoursRecords = getDockHourRecords(empId, dates);

        Collections.sort(docHoursRecords, dockHoursRecordDesc);

        if (docHoursRecords == null || docHoursRecords.size() == 0) {
            return BigDecimal.ZERO;
        } else {
            return docHoursRecords.get(0).getDockHours();
        }
    }
}
