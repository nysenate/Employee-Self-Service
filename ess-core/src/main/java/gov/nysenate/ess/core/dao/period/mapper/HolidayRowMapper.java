package gov.nysenate.ess.core.dao.period.mapper;

import gov.nysenate.ess.core.dao.base.BaseRowMapper;
import gov.nysenate.ess.core.model.period.Holiday;

import java.sql.ResultSet;
import java.sql.SQLException;

public class HolidayRowMapper extends BaseRowMapper<Holiday>
{
    protected String pfx;

    public HolidayRowMapper(String pfx) {
        this.pfx = pfx;
    }

    @Override
    public Holiday mapRow(ResultSet rs, int rowNum) throws SQLException {
        Holiday holiday = new Holiday();
        holiday.setActive(rs.getString("CDSTATUS").equals("A"));
        holiday.setDate(getLocalDateFromRs(rs, "DTHOLIDAY"));
        holiday.setName(rs.getString("DEHOLIDAY"));
        holiday.setQuestionable(rs.getString("CDQUEST").equals("Y"));
        holiday.setHours(rs.getBigDecimal("NUHOLHRS"));
        return holiday;
    }
}
