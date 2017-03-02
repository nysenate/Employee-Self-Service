package gov.nysenate.ess.time.dao.accrual.mapper;

import gov.nysenate.ess.core.dao.base.BaseRowMapper;
import gov.nysenate.ess.core.dao.period.mapper.PayPeriodRowMapper;
import gov.nysenate.ess.time.model.accrual.PeriodAccUsage;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PeriodAccUsageRowMapper extends BaseRowMapper<PeriodAccUsage>
{
    protected String pfx = "";
    protected PayPeriodRowMapper payPeriodRowMapper;

    public PeriodAccUsageRowMapper(String pfx, String payPeriodPfx) {
        this.pfx = pfx;
        this.payPeriodRowMapper = new PayPeriodRowMapper(payPeriodPfx);
    }

    @Override
    public PeriodAccUsage mapRow(ResultSet rs, int rowNum) throws SQLException {
        PeriodAccUsage accUsage = new PeriodAccUsage();
        accUsage.setYear(rs.getInt(pfx + "YEAR"));
        accUsage.setPayPeriod(payPeriodRowMapper.mapRow(rs, rowNum));
        AccrualUsageRowMapper.mapRow(rs, accUsage, pfx);
        return accUsage;
    }
}
