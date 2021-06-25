package gov.nysenate.ess.time.model.payroll;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.TreeSet;

public class Paycheck implements Comparable<Paycheck>
{
    private final String payPeriod;
    private final LocalDate checkDate;
    private final BigDecimal grossIncome;
    private final BigDecimal netIncome;
    private TreeSet<Deduction> deductions;
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
        this.deductions = new TreeSet<>();
    }

    /** Functional Methods */

    public void addDeduction(Deduction d) {
        this.deductions.add(d);
    }

    public BigDecimal getTotalDeductions() {
        return deductions.stream().map(Deduction::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Returns the deduction from the paycheck with the given code, or null if no deduction matches.
     * @param code
     * @return
     */
    public Deduction getDeduction(int code) {
        return deductions.stream()
                .filter(d -> d.getCode() == code)
                .findFirst()
                .orElse(null);
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

    public TreeSet<Deduction> getDeductions() {
        return deductions;
    }

    public BigDecimal getDirectDepositAmount() {
        return directDepositAmount;
    }

    public BigDecimal getCheckAmount() {
        return checkAmount;
    }

    @Override
    public String toString() {
        return "Paycheck{" +
                "payPeriod='" + payPeriod + '\'' +
                ", checkDate=" + checkDate +
                ", grossIncome=" + grossIncome +
                ", netIncome=" + netIncome +
                ", deductions=" + deductions +
                ", directDepositAmount=" + directDepositAmount +
                ", checkAmount=" + checkAmount +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Paycheck paycheck = (Paycheck) o;
        return Objects.equals(checkDate, paycheck.checkDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(checkDate);
    }

    @Override
    public int compareTo(Paycheck o) {
        return this.getCheckDate().compareTo(o.getCheckDate());
    }
}
