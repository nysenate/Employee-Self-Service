package gov.nysenate.ess.time.dao.accrual.mapper;

import gov.nysenate.ess.time.model.accrual.AccrualUsage;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * This row mapper doesn't implement the RowMapper interface but rather provides a static
 * method to map all the summary columns for a subclass of AccrualUsage.
 */
public class AccrualUsageRowMapper
{
    /**
     * Sets accrual usage columns on the supplied AccrualUsage object.
     * @throws java.sql.SQLException
     */
    public static void mapRow(ResultSet rs, AccrualUsage accUsage, String pfx) throws SQLException {
        if (accUsage == null) {
            throw new IllegalArgumentException("Accrual usage object cannot be null.");
        }
        accUsage.setEmpId(rs.getInt(pfx + "NUXREFEM"));
        accUsage.setWorkHours(rs.getBigDecimal(pfx + "WORK_HRS"));
        accUsage.setTravelHoursUsed(rs.getBigDecimal(pfx + "TRV_HRS_USED"));
        accUsage.setHolHoursUsed(rs.getBigDecimal(pfx + "HOL_HRS_USED"));
        accUsage.setVacHoursUsed(rs.getBigDecimal(pfx + "VAC_HRS_USED"));
        accUsage.setPerHoursUsed(rs.getBigDecimal(pfx + "PER_HRS_USED"));
        accUsage.setEmpHoursUsed(rs.getBigDecimal(pfx + "EMP_HRS_USED"));
        accUsage.setFamHoursUsed(rs.getBigDecimal(pfx + "FAM_HRS_USED"));
        accUsage.setMiscHoursUsed(rs.getBigDecimal(pfx + "MISC_HRS_USED"));
    }

    /**
     * Sets accrual usage columns on the supplied AccrualUsage object.
     * @throws java.sql.SQLException
     */
    public static void mapRow(ResultSet rs, AccrualUsage accrualUsage) throws SQLException {
        mapRow(rs, accrualUsage, "");
    }
}
