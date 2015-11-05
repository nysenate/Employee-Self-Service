package gov.nysenate.ess.seta.dao.allowances.mapper;

import gov.nysenate.ess.core.dao.base.BaseRowMapper;
import gov.nysenate.ess.seta.model.allowances.OldAllowanceUsage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

/**
 * This row mapper doesn't implement the RowMapper interface but rather provides a static
 * method to map all the summary columns for a subclass of Allowance.
 */
public class AllowanceRowMapper extends BaseRowMapper<OldAllowanceUsage>
{
    protected String pfx;

    private static final Logger logger = LoggerFactory.getLogger(AllowanceRowMapper.class);


    public AllowanceRowMapper(String pfx) {
        this.pfx = pfx;
    }
    /**
     * Sets accrual summary columns on the supplied AccrualSummary object.
     * @throws java.sql.SQLException
     */

    public OldAllowanceUsage mapRow(ResultSet rs, int rowNum) throws SQLException {
        OldAllowanceUsage allowanceUsage = new OldAllowanceUsage();
       /* logger.debug("Column Count:"+rs.getMetaData().getColumnCount());
        for (int x=0;x<rs.getMetaData().getColumnCount();x++) {
            logger.debug("AllowanceRowMapper mapRow Column:"+rs.getMetaData().getColumnName(x));
        }*/
        Date endDate = rs.getDate(pfx + "DTENDTE");
        int dtyear = new Integer(new SimpleDateFormat("yyyy").format(endDate)).intValue();

        allowanceUsage.setEmpId(rs.getInt(pfx + "NUXREFEM"));
        allowanceUsage.setMoneyUsed(rs.getBigDecimal(pfx + "TE_AMOUNT_PAID"));
        allowanceUsage.setHoursUsed(rs.getBigDecimal(pfx + "TE_HRS_PAID"));
        allowanceUsage.setEndDate(endDate);
        allowanceUsage.setYear(dtyear);
        return allowanceUsage;
    }
}
