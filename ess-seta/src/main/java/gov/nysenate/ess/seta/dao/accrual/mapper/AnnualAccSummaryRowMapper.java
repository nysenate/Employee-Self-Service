package gov.nysenate.ess.seta.dao.accrual.mapper;

import gov.nysenate.ess.core.dao.base.BaseRowMapper;
import gov.nysenate.ess.seta.model.accrual.AnnualAccSummary;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * RowMapper implementation for AnnualAccSummary.
 */
public class AnnualAccSummaryRowMapper extends BaseRowMapper<AnnualAccSummary>
{
    @Override
    public AnnualAccSummary mapRow(ResultSet rs, int rowNum) throws SQLException {
        AnnualAccSummary annAccRec = new AnnualAccSummary();
        annAccRec.setYear(rs.getInt("YEAR"));
        annAccRec.setCloseDate(getLocalDateFromRs(rs, "CLOSE_DATE"));
        annAccRec.setEndDate(getLocalDateFromRs(rs, "DTEND"));
        annAccRec.setContServiceDate(getLocalDateFromRs(rs, "CONT_SERVICE_DATE"));
        annAccRec.setPayPeriodsYtd(rs.getInt("PAY_PERIODS_YTD"));
        annAccRec.setPayPeriodsBanked(rs.getInt("PAY_PERIODS_BANKED"));
        AccrualSummaryRowMapper.mapRow(rs, annAccRec);
        return annAccRec;
    }
}