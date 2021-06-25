package gov.nysenate.ess.time.model.payroll;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

/**
 * Add summary information to a collection of Paychecks.
 * i.e. Totals, distinct deduction types, etc.
 * <p>
 * Deductions from {@link #deductions()} and deductions in individual paychecks
 * are sorted the same order and all paychecks will contain an entry for each deduction in {@link #deductions()}.
 */
public class PaychecksSummary {

    private final TreeSet<Paycheck> paychecks;

    public PaychecksSummary(Collection<Paycheck> paychecks) {
        this.paychecks = new TreeSet<>(paychecks);
    }

    /**
     * Get all distinct deductions applied to any paycheck.
     *
     * @return A sorted set of deductions.
     */
    public TreeSet<Deduction> deductions() {
        TreeSet<Deduction> distinctDeductions = new TreeSet<>();
        for (Paycheck paycheck : paychecks) {
            distinctDeductions.addAll(paycheck.getDeductions());
        }

        return distinctDeductions;
    }

    /**
     * Get the paychecks used in this summary.
     * <p>
     * Ensures that each paycheck has the same deductions by adding a deduction with amount = 0 to
     * each paycheck for every deduction from {@link #deductions()} it's missing.
     *
     * @return A set of Paychecks ordered by checkDate.
     */
    public TreeSet<Paycheck> paychecks() {
        for (Paycheck paycheck : paychecks) {
            normalizePaycheck(paycheck);
        }
        return paychecks;
    }

    private void normalizePaycheck(Paycheck paycheck) {
        for (Deduction deduction : deductions()) {
            if (!paycheck.getDeductions().contains(deduction)) {
                paycheck.addDeduction(zeroDeductionCopy(deduction));
            }
        }
    }

    // Returns a copy of this deduction but with a amount = 0.
    private Deduction zeroDeductionCopy(Deduction deduction) {
        return new Deduction(deduction.getCode(), deduction.getOrder(), deduction.getDescription(), BigDecimal.ZERO);
    }

    /**
     * Get a Map of deduction code to deduction total for all paychecks.
     * @return
     */
    public Map<Integer, BigDecimal> deductionTotals() {
        var deductionCodeToTotal = new HashMap<Integer, BigDecimal>();
        for (var deduction : deductions()) {
            BigDecimal total = BigDecimal.ZERO;
            for (var paycheck : this.paychecks) {
                var paycheckDeduction = paycheck.getDeduction(deduction.getCode());
                if (paycheckDeduction != null) {
                    total = total.add(paycheckDeduction.getAmount());
                }
            }
            deductionCodeToTotal.put(deduction.getCode(), total);
        }
        return deductionCodeToTotal;
    }

    /**
     * @return The total gross income for all paychecks
     */
    public BigDecimal grossIncomeTotal() {
        return paychecks.stream()
                .map(Paycheck::getGrossIncome)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * @return The total net income for all paychecks
     */
    public BigDecimal netIncomeTotal() {
        return paychecks.stream()
                .map(Paycheck::getNetIncome)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * @return The total direct deposit amount for all paychecks.
     */
    public BigDecimal directDepositTotal() {
        return paychecks.stream()
                .map(Paycheck::getDirectDepositAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * @return The total check amount for all paychecks.
     */
    public BigDecimal checkAmountTotal() {
        return paychecks.stream()
                .map(Paycheck::getCheckAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
