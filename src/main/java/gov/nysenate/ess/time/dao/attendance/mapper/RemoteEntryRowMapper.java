package gov.nysenate.ess.time.dao.attendance.mapper;

import gov.nysenate.ess.core.dao.base.BaseRowMapper;
import gov.nysenate.ess.time.model.attendance.TimeEntry;
import gov.nysenate.ess.time.model.payroll.MiscLeaveType;
import gov.nysenate.ess.core.model.payroll.PayType;

import java.sql.ResultSet;
import java.sql.SQLException;

public class RemoteEntryRowMapper extends BaseRowMapper<TimeEntry>
{
    private String pfx = "";

    public RemoteEntryRowMapper() {}

    public RemoteEntryRowMapper(String pfx) {
        this.pfx = pfx;
    }

    @Override
    public TimeEntry mapRow(ResultSet rs, int rowNum) throws SQLException {
        TimeEntry te = new TimeEntry();
        te.setEntryId(rs.getBigDecimal(pfx + "NUXRDAY").toBigInteger());
        te.setTimeRecordId(rs.getBigDecimal(pfx + "NUXRTIMESHEET").toBigInteger());
        te.setEmpId(rs.getInt(pfx + "NUXREFEM"));
        te.setEmployeeName(rs.getString(pfx + "NAUSER"));
        te.setDate(getLocalDateFromRs(rs, pfx + "DTDAY"));
        te.setWorkHours(rs.getBigDecimal(pfx + "NUWORK"));
        te.setTravelHours(rs.getBigDecimal(pfx + "NUTRAVEL"));
        te.setHolidayHours(rs.getBigDecimal(pfx + "NUHOLIDAY"));
        te.setVacationHours(rs.getBigDecimal(pfx + "NUVACATION"));
        te.setPersonalHours(rs.getBigDecimal(pfx + "NUPERSONAL"));
        te.setSickEmpHours(rs.getBigDecimal(pfx + "NUSICKEMP"));
        te.setSickFamHours(rs.getBigDecimal(pfx + "NUSICKFAM"));
        te.setMiscHours(rs.getBigDecimal(pfx + "NUMISC"));
        if (rs.getString(pfx + "NUXRMISC") != null) {
            te.setMiscType(MiscLeaveType.valueOfId(rs.getBigDecimal(pfx + "NUXRMISC").toBigInteger()));
        }
        te.setMisc2Hours(rs.getBigDecimal(pfx + "NUMISC2"));
        if (rs.getString(pfx + "NUXRMISC2") != null) {
            te.setMiscType2(MiscLeaveType.valueOfId(rs.getBigDecimal(pfx + "NUXRMISC2").toBigInteger()));
        }
        te.setOriginalUserId(rs.getString(pfx + "NATXNORGUSER"));
        te.setUpdateUserId(rs.getString(pfx + "NATXNUPDUSER"));
        te.setOriginalDate(getLocalDateTimeFromRs(rs, pfx + "DTTXNORIGIN"));
        te.setUpdateDate(getLocalDateTimeFromRs(rs, pfx + "DTTXNUPDATE"));
        te.setActive(rs.getString(pfx + "CDSTATUS").equals("A"));
        te.setEmpComment(rs.getString(pfx + "DECOMMENTS"));
        String payTypeStr = rs.getString(pfx + "CDPAYTYPE");
        te.setPayType(payTypeStr != null ? PayType.valueOf(payTypeStr) : null);
        return te;
    }
}
