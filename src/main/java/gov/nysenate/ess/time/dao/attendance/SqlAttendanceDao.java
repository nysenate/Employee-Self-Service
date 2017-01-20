package gov.nysenate.ess.time.dao.attendance;

import com.google.common.collect.*;
import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import gov.nysenate.ess.core.util.DateUtils;
import gov.nysenate.ess.core.util.OrderBy;
import gov.nysenate.ess.core.util.SortOrder;
import gov.nysenate.ess.time.dao.attendance.mapper.AttendanceRecordRowMapper;
import gov.nysenate.ess.time.model.attendance.AttendanceRecord;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

@Service
public class SqlAttendanceDao extends SqlBaseDao implements AttendanceDao
{
    /** {@inheritDoc} */
    @Override
    public SortedSet<Integer> getOpenAttendanceYears(Integer empId) {
        return new TreeSet<>(
                remoteNamedJdbc.query(SqlAttendanceQuery.GET_OPEN_ATTENDANCE_YEARS.getSql(schemaMap()),
                        new MapSqlParameterSource("empId", empId), ((rs, rowNum) -> rs.getInt("year")))
        );
    }

    /** {@inheritDoc} */
    @Override
    public RangeSet<LocalDate> getOpenDates(Integer empId) {
        RangeSet<LocalDate> activeDates = TreeRangeSet.create();
        getOpenAttendanceYears(empId).stream()
                .map(year -> LocalDate.now().getYear() == year
                        ? Range.closedOpen(LocalDate.ofYearDay(year, 1), LocalDate.now().plusDays(2))
                        : Range.closedOpen(LocalDate.ofYearDay(year, 1), LocalDate.ofYearDay(year + 1, 1)))
                .forEach(activeDates::add);
        return activeDates;
    }

    /** {@inheritDoc} */
    @Override
    public SortedSet<Integer> getAttendanceYears(Integer empId) {
        return new TreeSet<>(
                remoteNamedJdbc.query(SqlAttendanceQuery.GET_ALL_ATTENDANCE_YEARS.getSql(schemaMap()),
                        new MapSqlParameterSource("empId", empId), ((rs, rowNum) -> rs.getInt("DTPERIODYEAR")))
        );
    }

    /** {@inheritDoc} */
    @Override
    public ListMultimap<Integer, AttendanceRecord> getOpenAttendanceRecords() {
        ListMultimap<Integer, AttendanceRecord> openRecords = ArrayListMultimap.create();
        remoteNamedJdbc.query(SqlAttendanceQuery.GET_OPEN_ATTENDANCE_RECORDS.getSql(schemaMap()), new AttendanceRecordRowMapper())
                .forEach(attRec -> openRecords.put(attRec.getEmployeeId(), attRec));
        return openRecords;
    }

    /** {@inheritDoc} */
    @Override
    public List<AttendanceRecord> getOpenAttendanceRecords(Integer empId) {
        MapSqlParameterSource params = new MapSqlParameterSource("empId", empId);
        return remoteNamedJdbc.query(SqlAttendanceQuery.GET_OPEN_ATTENDANCE_RECORDS_FOR_EMPID.getSql(schemaMap()),
                params, new AttendanceRecordRowMapper());
    }

    /** {@inheritDoc} */
    @Override
    public List<AttendanceRecord> getAttendanceRecords(Integer empId, Integer year) {
        MapSqlParameterSource params = new MapSqlParameterSource("empId", empId)
                .addValue("year", year);
        return remoteNamedJdbc.query(SqlAttendanceQuery.GET_ATTENDANCE_RECORDS_FOR_YEAR.getSql(
                        schemaMap()),
                params, new AttendanceRecordRowMapper());
    }

    /** {@inheritDoc} */
    @Override
    public List<AttendanceRecord> getAttendanceRecords(Integer empId, Range<LocalDate> dates) {
        MapSqlParameterSource params = new MapSqlParameterSource("empId", empId)
                .addValue("startDate", toDate(DateUtils.startOfDateRange(dates)))
                .addValue("endDate", toDate(DateUtils.endOfDateRange(dates)));
        return remoteNamedJdbc.query(SqlAttendanceQuery.GET_ATTENDANCE_RECORDS_FOR_DATES.getSql(
                        schemaMap()),
                params, new AttendanceRecordRowMapper());
    }
}
