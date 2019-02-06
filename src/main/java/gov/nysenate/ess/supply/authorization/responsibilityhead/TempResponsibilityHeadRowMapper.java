package gov.nysenate.ess.supply.authorization.responsibilityhead;

import gov.nysenate.ess.core.dao.base.BaseRowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TempResponsibilityHeadRowMapper extends BaseRowMapper<TempResponsibilityHead> {

    @Override
    public TempResponsibilityHead mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new TempResponsibilityHead(
                rs.getInt("id"),
                rs.getInt("employee_id"),
                rs.getString("rch_code"),
                getLocalDateTimeFromRs(rs, "start_date"),
                getLocalDateTimeFromRs(rs, "end_date")
        );
    }
}
