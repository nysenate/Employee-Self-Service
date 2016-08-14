package gov.nysenate.ess.time.dao.allowances.mapper;

import gov.nysenate.ess.core.dao.base.BaseRowMapper;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * This row mapper doesn't implement the RowMapper interface but rather provides a static
 * method to map all the summary columns for a subclass of Allowance.
 */
public class AmountExceedRowMapper extends BaseRowMapper<BigDecimal>
{
    protected String pfx;

    public AmountExceedRowMapper(String pfx) {
        this.pfx = pfx;
    }
    /**
     * Sets accrual summary columns on the supplied AccrualSummary object.
     * @throws java.sql.SQLException
     */

    public  BigDecimal mapRow(ResultSet rs, int rowNum) throws SQLException {
        BigDecimal amountExceed = null;
        amountExceed =rs.getBigDecimal(pfx + "MOAMTEXCEED");
        return amountExceed;
    }
}
