package gov.nysenate.ess.seta.dao.accrual.mapper;

import gov.nysenate.ess.core.dao.base.BaseRowMapper;
import gov.nysenate.ess.seta.model.accrual.AnnualAccrualUsage;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AnnualAccrualUsageRowMapper extends BaseRowMapper<AnnualAccrualUsage>
{
    @Override
    public AnnualAccrualUsage mapRow(ResultSet rs, int rowNum) throws SQLException {
        AnnualAccrualUsage annAccUsage = new AnnualAccrualUsage();
        annAccUsage.setLatestStartDate(getLocalDateFromRs(rs, "LATEST_DTBEGIN"));
        annAccUsage.setLatestEndDate(getLocalDateFromRs(rs, "LATEST_DTEND"));
        AccrualUsageRowMapper.mapRow(rs, annAccUsage);
        return annAccUsage;
    }
}
