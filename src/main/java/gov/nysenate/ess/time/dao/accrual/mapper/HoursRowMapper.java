package gov.nysenate.ess.time.dao.accrual.mapper;

import gov.nysenate.ess.core.dao.base.BaseRowMapper;
import gov.nysenate.ess.time.model.accrual.Hours;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * This row mapper doesn't implement the RowMapper interface but rather provides a static
 * method to map all the summary columns for a subclass of AccrualSummary.
 */
public class HoursRowMapper extends BaseRowMapper<Hours>
{
    @Override
    public Hours mapRow(ResultSet rs, int rowNum) throws SQLException {
        Hours hours = new Hours();
        hours.setEndDate(rs.getDate(1));
        hours.setHours(rs.getBigDecimal(2));
        return hours;
    }
}