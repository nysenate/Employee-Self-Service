package gov.nysenate.ess.web.dao.accrual.mapper;

import gov.nysenate.ess.core.dao.base.BaseRowMapper;
import gov.nysenate.ess.seta.model.accrual.AccrualUsage;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by riken on 4/16/14.
 */
public class LocalAccrualUsageRowMapper extends BaseRowMapper<AccrualUsage>
{

        @Override
        public AccrualUsage mapRow(ResultSet rs, int rowNum) throws SQLException {
            AccrualUsage accUsage = new AccrualUsage();
            accUsage.setEmpId(rs.getInt("t_emp_id"));
            accUsage.setWorkHours(rs.getBigDecimal("t_work"));
            accUsage.setTravelHoursUsed(rs.getBigDecimal("t_travel"));
            accUsage.setHolHoursUsed(rs.getBigDecimal("t_holiday"));
            accUsage.setEmpHoursUsed(rs.getBigDecimal("t_sick_emp"));
            accUsage.setFamHoursUsed(rs.getBigDecimal("t_sick_family"));
            accUsage.setMiscHoursUsed(rs.getBigDecimal("t_misc"));
            accUsage.setVacHoursUsed(rs.getBigDecimal("t_vacation"));
            accUsage.setPerHoursUsed(rs.getBigDecimal("t_personal"));
            return accUsage;
        }
    }


