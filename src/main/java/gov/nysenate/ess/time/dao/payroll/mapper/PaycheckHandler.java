package gov.nysenate.ess.time.dao.payroll.mapper;

import gov.nysenate.ess.core.dao.base.BaseHandler;
import gov.nysenate.ess.time.model.payroll.Deduction;
import gov.nysenate.ess.time.model.payroll.Paycheck;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PaycheckHandler extends BaseHandler
{
    private HashMap<LocalDate, Paycheck> dateToPaycheck = new HashMap<>();

    public PaycheckHandler() {
        dateToPaycheck = new HashMap<>();
    }

    @Override
    public void processRow(ResultSet rs) throws SQLException {
        LocalDate date = getLocalDateFromRs(rs, "DTCHECK");
        Paycheck paycheck = dateToPaycheck.get(date);
        if (paycheck == null) {
            paycheck = paycheckRowMapper.mapRow(rs, rs.getRow());
            dateToPaycheck.put(date, paycheck);
        }
        Deduction deduction = deductionRowMapper.mapRow(rs, rs.getRow());
        paycheck.addDeduction(deduction);
    }

    public List<Paycheck> getPaychecks() {
        return new ArrayList<>(dateToPaycheck.values());
    }

    private static final RowMapper<Paycheck> paycheckRowMapper = ((rs, rowNum) ->
            new Paycheck(
                    rs.getString("NUPERIOD"),
                    getLocalDateFromRs(rs, "DTCHECK"),
                    rs.getBigDecimal("MOGROSS"),
                    rs.getBigDecimal("MONET"),
                    rs.getBigDecimal("MOADVICEAMT"),
                    rs.getBigDecimal("MOCHECKAMT")
            )
    );

    private static final RowMapper<Deduction> deductionRowMapper = ((rs, rowNum) ->
            new Deduction(
                    rs.getString("CDDEDUCTION"),
                    rs.getInt("CDORDER"),
                    rs.getString("DEDEDUCTIONF"),
                    rs.getBigDecimal("MODEDUCTION")
            )
    );
}
