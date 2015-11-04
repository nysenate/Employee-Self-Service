package gov.nysenate.ess.web.dao.accrual.mapper;

import gov.nysenate.ess.core.dao.base.BaseRowMapper;
import gov.nysenate.ess.core.dao.period.mapper.PayPeriodRowMapper;
import gov.nysenate.ess.seta.model.accrual.PeriodAccSummary;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PeriodAccSummaryRowMapper extends BaseRowMapper<PeriodAccSummary>
{
    protected String pfx = "";
    protected PayPeriodRowMapper payPeriodRowMapper;

    public PeriodAccSummaryRowMapper(String pfx, String payPeriodPfx) {
        this.pfx = pfx;
        this.payPeriodRowMapper = new PayPeriodRowMapper(payPeriodPfx);
    }

    @Override
    public PeriodAccSummary mapRow(ResultSet rs, int rowNum) throws SQLException {
        PeriodAccSummary perAccSum = new PeriodAccSummary();
        perAccSum.setComputed(false);
        perAccSum.setYear(rs.getInt(pfx + "YEAR"));
        perAccSum.setPrevTotalHoursYtd(rs.getBigDecimal(pfx + "PREV_TOTAL_HRS"));
        perAccSum.setExpectedTotalHours(rs.getBigDecimal(pfx + "EXPECTED_TOTAL_HRS"));
        perAccSum.setExpectedBiweekHours(rs.getBigDecimal(pfx + "EXPECTED_BIWEEK_HRS"));
        perAccSum.setSickRate(rs.getBigDecimal(pfx + "SICK_RATE"));
        perAccSum.setVacRate(rs.getBigDecimal(pfx + "VAC_RATE"));
        perAccSum.setRefPayPeriod(payPeriodRowMapper.mapRow(rs, rowNum));
        perAccSum.setPayPeriod(perAccSum.getRefPayPeriod());
        AccrualSummaryRowMapper.mapRow(rs, perAccSum);
        return perAccSum;
    }
}
