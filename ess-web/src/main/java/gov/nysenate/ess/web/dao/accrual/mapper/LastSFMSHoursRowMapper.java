package gov.nysenate.ess.web.dao.accrual.mapper;


import gov.nysenate.ess.core.dao.base.BaseRowMapper;
import gov.nysenate.ess.web.model.accrual.Hours;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * This row mapper doesn't implement the RowMapper interface but rather provides a static
 * method to map all the summary columns for a subclass of AccrualSummary.
 */
public class LastSFMSHoursRowMapper extends BaseRowMapper<Hours>
{
    @Override
    public Hours mapRow(ResultSet rs, int rowNum) throws SQLException {
        Hours hours = new Hours();
        BigDecimal totalHours = rs.getBigDecimal(2);
        totalHours = totalHours.add(rs.getBigDecimal(3));
        hours.setEndDate(rs.getDate(1));
        hours.setHours(totalHours);

        return hours;
    }
}