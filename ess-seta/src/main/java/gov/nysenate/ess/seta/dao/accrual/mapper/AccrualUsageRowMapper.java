package gov.nysenate.ess.seta.dao.accrual.mapper;

import gov.nysenate.ess.seta.model.accrual.AccrualUsage;

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
    public static void mapRow(ResultSet rs, AccrualUsage accUsage) throws SQLException {
        if (accUsage == null) {
            throw new IllegalArgumentException("Accrual usage object cannot be null.");
        }
        accUsage.setEmpId(rs.getInt("NUXREFEM"));
        accUsage.setWorkHours(rs.getBigDecimal("WORK_HRS"));
        accUsage.setTravelHoursUsed(rs.getBigDecimal("TRV_HRS_USED"));
        accUsage.setHolHoursUsed(rs.getBigDecimal("HOL_HRS_USED"));
        accUsage.setVacHoursUsed(rs.getBigDecimal("VAC_HRS_USED"));
        accUsage.setPerHoursUsed(rs.getBigDecimal("PER_HRS_USED"));
        accUsage.setEmpHoursUsed(rs.getBigDecimal("EMP_HRS_USED"));
        accUsage.setFamHoursUsed(rs.getBigDecimal("FAM_HRS_USED"));
        accUsage.setMiscHoursUsed(rs.getBigDecimal("MISC_HRS_USED"));
    }
}
