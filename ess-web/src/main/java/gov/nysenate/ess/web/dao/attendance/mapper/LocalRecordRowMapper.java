package gov.nysenate.ess.web.dao.attendance.mapper;

import gov.nysenate.ess.web.model.attendance.TimeRecord;
import gov.nysenate.ess.core.dao.base.BaseRowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class LocalRecordRowMapper extends BaseRowMapper<TimeRecord>
{
    private String pfx = "";

    public LocalRecordRowMapper(String pfx){
        this.pfx = pfx;
    }

    @Override
    public TimeRecord mapRow(ResultSet rs, int rowNum) throws SQLException {
        TimeRecord record = new TimeRecord();
        // TODO update for changes in TimeRecord
//        record.setTimeRecordId(rs.getString(pfx + "time_record_id"));
//        record.setEmployeeId(rs.getInt(pfx + "emp_id"));
//        record.setOriginalUserId(rs.getString(pfx + "t_original_user"));
//        record.setUpdateUserId(rs.getString(pfx + "t_update_user"));
//        record.setCreatedDate(rs.getTimestamp(pfx + "t_original_date"));
//        record.setUpdateDate(rs.getTimestamp(pfx + "t_update_date"));
//        record.setActive(rs.getString(pfx + "status").equals("A"));
//        record.setRecordStatus(TimeRecordStatus.valueOfCode(rs.getString(pfx + "ts_status_id")));
//        record.setBeginDate(rs.getDate(pfx + "begin_date"));
//        record.setEndDate(rs.getDate(pfx + "end_date"));
//        record.setRemarks(rs.getString(pfx + "remarks"));
//        record.setExceptionDetails(rs.getString(pfx + "exc_details"));
//        record.setProcessedDate(rs.getDate(pfx + "proc_date"));
        return record;
    }
}
