package gov.nysenate.ess.time.dao.attendance.mapper;


import gov.nysenate.ess.core.dao.base.BaseRowMapper;
import gov.nysenate.ess.time.model.attendance.TimeOffRequestDay;
import gov.nysenate.ess.time.model.payroll.MiscLeaveType;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class TimeOffRequestDayRowMapper extends BaseRowMapper<TimeOffRequestDay> {

    public TimeOffRequestDayRowMapper() {}

    @Override
    public TimeOffRequestDay mapRow(ResultSet rs, int rowNum) throws SQLException {
        TimeOffRequestDay day = new TimeOffRequestDay();
        day.setRequestId(rs.getInt("request_id"));
        day.setDate(rs.getDate("request_date").toLocalDate());
        day.setWorkHours(rs.getBigDecimal("work_hours"));
        day.setHolidayHours(rs.getBigDecimal("holiday_hours"));
        day.setVacationHours(rs.getBigDecimal("vacation_hours"));
        day.setPersonalHours(rs.getBigDecimal("personal_hours"));
        day.setSickEmpHours(rs.getBigDecimal("sick_emp_hours"));
        day.setSickFamHours(rs.getBigDecimal("sick_fam_hours"));
        day.setMiscHours(rs.getBigDecimal("misc_hours"));
        //Allow for misc_type to be a null value
        day.setMiscType(Optional.ofNullable(rs.getString("misc_type"))
                .map(MiscLeaveType::valueOf)
                .orElse(null));
        return day;
    }
}
