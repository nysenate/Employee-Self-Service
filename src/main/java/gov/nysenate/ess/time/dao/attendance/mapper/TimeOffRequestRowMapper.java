package gov.nysenate.ess.time.dao.attendance.mapper;

import gov.nysenate.ess.core.dao.base.BaseRowMapper;
import gov.nysenate.ess.time.model.attendance.TimeOffRequest;
import gov.nysenate.ess.time.model.attendance.TimeOffStatus;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TimeOffRequestRowMapper extends BaseRowMapper<TimeOffRequest> {

    public TimeOffRequestRowMapper() {}

    @Override
    public TimeOffRequest mapRow(ResultSet rs, int rowNum) throws SQLException {
        TimeOffRequest tor = new TimeOffRequest();
        tor.setRequestId(rs.getInt("request_id"));
        tor.setEmployeeId(rs.getInt("employee_id"));
        tor.setSupervisorId(rs.getInt("supervisor_id"));
        tor.setStatus(TimeOffStatus.valueOf(rs.getString("status")));
        tor.setTimestamp(rs.getTimestamp("update_timestamp"));
        tor.setStartDate(rs.getDate("start_date"));
        tor.setEndDate(rs.getDate("end_date"));
        tor.setComments(null);
        tor.setDays(null);
        return tor;
    }
}
