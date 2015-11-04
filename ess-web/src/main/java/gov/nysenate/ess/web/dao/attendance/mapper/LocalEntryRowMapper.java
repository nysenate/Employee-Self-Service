package gov.nysenate.ess.web.dao.attendance.mapper;

import gov.nysenate.ess.core.dao.base.BaseRowMapper;
import gov.nysenate.ess.web.model.attendance.TimeEntry;

import java.sql.ResultSet;
import java.sql.SQLException;

public class LocalEntryRowMapper extends BaseRowMapper<TimeEntry>
{
    private String pfx="";

    public LocalEntryRowMapper(String pfx){
        this.pfx = pfx;
    }

    @Override
    public TimeEntry mapRow(ResultSet rs, int rowNum) throws SQLException {
        // TODO move to local entry dao
        TimeEntry entry = new TimeEntry();
//        entry.setEntryId(rs.getBigDecimal(pfx + "time_entry_id").toBigInteger());
//        entry.setTimeRecordId(rs.getBigDecimal(pfx + "time_record_id").toBigInteger());
//        entry.setEmpId(rs.getInt(pfx + "emp_id"));
//        entry.setDate(rs.getDate(pfx + "day_date"));
//        entry.setWorkHours(rs.getInt(pfx + "work_hr"));
//        entry.setTravelHours(rs.getInt(pfx + "travel_hr"));
//        entry.setHolidayHours(rs.getInt(pfx + "holiday_hr"));
//        entry.setSickEmpHours(rs.getInt(pfx + "sick_emp_hr"));
//        entry.setSickFamHours(rs.getInt(pfx + "sick_family_hr"));
//        entry.setMiscHours(rs.getInt(pfx + "misc_hr"));
//        if (rs.getString(pfx + "misc_type") != null) {
//            entry.setMiscType(MiscLeaveType.valueOf(rs.getString(pfx + "misc_type")));
//        }
//        entry.setOriginalUserId(rs.getString(pfx + "tx_original_user"));
//        entry.setUpdateUserId(rs.getString(pfx + "tx_update_user"));
//        entry.setCreatedDate(rs.getTimestamp(pfx + "tx_original_date"));
//        entry.setUpdateDate(rs.getTimestamp(pfx + "tx_update_date"));
//        entry.setActive(rs.getString(pfx + "status").equals("A"));
//        entry.setEmpComment(rs.getString(pfx + "emp_comment"));
//        entry.setPayType(PayType.valueOf(rs.getString(pfx + "pay_type")));
//        entry.setVacationHours(rs.getInt(pfx + "vacation_hr"));
//        entry.setPersonalHours(rs.getInt(pfx + "personal_hr"));
        return entry;
    }
}