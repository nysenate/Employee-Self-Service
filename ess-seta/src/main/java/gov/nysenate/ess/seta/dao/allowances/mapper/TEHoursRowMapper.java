package gov.nysenate.ess.seta.dao.allowances.mapper;

import gov.nysenate.ess.core.dao.base.BaseRowMapper;
import gov.nysenate.ess.seta.model.allowances.TEHours;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * This row mapper doesn't implement the RowMapper interface but rather provides a static
 * method to map all the summary columns for a subclass of Allowance.
 */
public class TEHoursRowMapper extends BaseRowMapper<TEHours>
{
    protected String pfx;

    private static final Logger logger = LoggerFactory.getLogger(TEHoursRowMapper.class);


    public TEHoursRowMapper(String pfx) {
        this.pfx = pfx;
    }
    /**
     * Sets accrual summary columns on the supplied AccrualSummary object.
     * @throws java.sql.SQLException
     */

    public  TEHours mapRow(ResultSet rs, int rowNum) throws SQLException {
        TEHours tEHours = new TEHours();
       /* logger.debug("Column Count:"+rs.getMetaData().getColumnCount());
        for (int x=0;x<rs.getMetaData().getColumnCount();x++) {
            logger.debug("AllowanceRowMapper mapRow Column:"+rs.getMetaData().getColumnName(x));
        }*/
        Date beginDate = rs.getDate(pfx + "DTENDTE");
        Date endDate = rs.getDate(pfx + "DTENDTE");

        tEHours.setEmpId(rs.getInt(pfx + "NUXREFEM"));
        tEHours.setBeginDate(beginDate);
        tEHours.setEndDate(endDate);
        tEHours.setTEHours(rs.getBigDecimal(pfx + "NUHRHRSPD"));
        tEHours.setHourStatus(tEHours.PAID);

        return tEHours;
    }
}
