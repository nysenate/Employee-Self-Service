package gov.nysenate.ess.seta.dao.payroll.mapper;

import gov.nysenate.ess.core.dao.base.BaseHandler;
import gov.nysenate.ess.seta.model.payroll.Deduction;
import gov.nysenate.ess.seta.model.payroll.Paycheck;

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
        if (dateToPaycheck.containsKey(date)) {
            Deduction deduction = new Deduction(
                    rs.getString("CDDEDUCTION"), rs.getString("DEDEDUCTIONF"), rs.getBigDecimal("MODEDUCTION"));
            dateToPaycheck.get(date).addDeduction(deduction);
        }
        else {
            Paycheck paycheck = new Paycheck(rs.getString("NUPERIOD"), getLocalDateFromRs(rs, "DTCHECK"),
                                             rs.getBigDecimal("MOGROSS"), rs.getBigDecimal("MONET"),
                                             rs.getBigDecimal("MOADVICEAMT"), rs.getBigDecimal("MOCHECKAMT"));
            Deduction deduction = new Deduction(
                    rs.getString("CDDEDUCTION"), rs.getString("DEDEDUCTIONF"), rs.getBigDecimal("MODEDUCTION"));
            paycheck.addDeduction(deduction);
            dateToPaycheck.put(date, paycheck);
        }
    }

    public List<Paycheck> getPaychecks() {
        return new ArrayList<>(dateToPaycheck.values());
    }
}
