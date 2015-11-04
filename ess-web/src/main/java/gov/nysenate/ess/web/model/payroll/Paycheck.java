package gov.nysenate.ess.web.model.payroll;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Paycheck
{
    private final String payPeriod;
    private final LocalDate checkDate;
    private final BigDecimal grossIncome;
    private final BigDecimal netIncome;
    private List<Deduction> deductions;
    /** Amount payed via Direct Deposit. */
    private final BigDecimal directDepositAmount;
    /** Amount payed via check. */
    private final BigDecimal checkAmount;

    public Paycheck(String payPeriod, LocalDate checkDate, BigDecimal grossIncome, BigDecimal netIncome,
                    BigDecimal directDepositAmount, BigDecimal checkAmount) {
        this.payPeriod = payPeriod;
        this.checkDate = checkDate;
        this.grossIncome = grossIncome;
        this.netIncome = netIncome;
        this.directDepositAmount = directDepositAmount;
        this.checkAmount = checkAmount;
        this.deductions = new ArrayList<>();
    }

    /** Functional Methods */

    public void addDeduction(Deduction d) {
        this.deductions.add(d);
    }

    public BigDecimal getTotalDeductions() {
        return deductions.stream().map(Deduction::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /** Basic Getters */

    public String getPayPeriod() {
        return payPeriod;
    }

    public LocalDate getCheckDate() {
        return checkDate;
    }

    public BigDecimal getGrossIncome() {
        return grossIncome;
    }

    public BigDecimal getNetIncome() {
        return netIncome;
    }

    public List<Deduction> getDeductions() {
        return deductions;
    }

    public BigDecimal getDirectDepositAmount() {
        return directDepositAmount;
    }

    public BigDecimal getCheckAmount() {
        return checkAmount;
    }

}
