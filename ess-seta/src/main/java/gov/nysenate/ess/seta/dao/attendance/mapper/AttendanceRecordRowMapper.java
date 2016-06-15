package gov.nysenate.ess.seta.dao.attendance.mapper;

import gov.nysenate.ess.core.util.DateUtils;
import gov.nysenate.ess.core.dao.base.BaseRowMapper;
import gov.nysenate.ess.seta.model.attendance.AttendanceRecord;
import org.apache.commons.lang3.StringUtils;

import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Year;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

public class AttendanceRecordRowMapper extends BaseRowMapper<AttendanceRecord>
{
    @Override
    public AttendanceRecord mapRow(ResultSet rs, int rowNum) throws SQLException {
        AttendanceRecord record = new AttendanceRecord();
        record.setEmployeeId(rs.getInt("NUXREFEM"));
        record.setActive(StringUtils.equals("A", rs.getString("CDSTATUS")));
        record.setBeginDate(DateUtils.getLocalDate(rs.getDate("DTBEGIN")));
        record.setEndDate(DateUtils.getLocalDate(rs.getDate("DTEND")));
        record.setYear(Year.parse(rs.getString("DTPERIODYEAR")));
        record.setPayPeriodNum(rs.getString("NUPERIOD"));
        record.setPostDate(DateUtils.getLocalDateTime(rs.getTimestamp("DTPOST")));
        record.setCreatedDate(DateUtils.getLocalDateTime(rs.getTimestamp("DTTXNORIGIN")));
        record.setUpdatedDate(DateUtils.getLocalDateTime(rs.getTimestamp("DTTXNUPDATE")));
        record.setTransactionNote(rs.getString("DETXNNOTE50"));
        record.setTimesheetIds(Optional.ofNullable(rs.getString("NUXRTIMESHEETS"))
                .map(ids -> StringUtils.split(ids, ','))
                .map(idArray -> Arrays.stream(idArray).map(BigInteger::new).collect(Collectors.toList()))
                .orElse(Collections.emptyList()));
        record.setExpectedDays(rs.getInt("NUDAYEXPECT"));

        record.setWorkHours(rs.getBigDecimal("NUWORKHRS"));
        record.setTravelHours(rs.getBigDecimal("NUTRVHRS"));
        record.setHolidayHours(rs.getBigDecimal("NUHOLHRS"));
        record.setVacationHours(rs.getBigDecimal("NUVACHRS"));
        record.setPersonalHours(rs.getBigDecimal("NUPERHRS"));
        record.setSickEmpHours(rs.getBigDecimal("NUEMPHRS"));
        record.setSickFamHours(rs.getBigDecimal("NUFAMHRS"));
        record.setMiscHours(rs.getBigDecimal("NUMISCHRS"));
        return record;
    }
}