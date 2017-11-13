package gov.nysenate.ess.time.model.allowances;

import com.google.common.collect.Range;
import gov.nysenate.ess.core.model.period.PayPeriod;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static java.math.BigDecimal.ZERO;

/**
 * @author Sam Stouffer
 *
 * Represents {@link AllowanceUsage} for a specific pay period
 */
public class PeriodAllowanceUsage extends AllowanceUsage {

    private PayPeriod payPeriod;

    private BigDecimal periodHoursUsed = ZERO;
    private BigDecimal periodMoneyUsed = ZERO;

    public PeriodAllowanceUsage(int empId, int year, PayPeriod payPeriod) {
        super(empId, year,
                Optional.ofNullable(payPeriod).map(PayPeriod::getEndDate).orElse(null));
        this.payPeriod = payPeriod;
    }

    public PeriodAllowanceUsage(PeriodAllowanceUsage periodAllowanceUsage) {
        super(periodAllowanceUsage);
        this.payPeriod = periodAllowanceUsage.payPeriod;
        this.periodHoursUsed = periodAllowanceUsage.periodHoursUsed;
        this.periodMoneyUsed = periodAllowanceUsage.periodMoneyUsed;
    }

    /* --- Overrides --- */

    @Override
    public Range<LocalDate> getEffectiveRange() {
        return Optional.ofNullable(payPeriod).map(PayPeriod::getDateRange).orElse(null);
    }

    /* --- Functional Getters / Setters */

    public PeriodAllowanceUsage getNextPerAllowanceUsage(PayPeriod nextPeriod) {
        PeriodAllowanceUsage nextPeriodAllowanceUsage = new PeriodAllowanceUsage(empId, year, nextPeriod);
        nextPeriodAllowanceUsage.recordHoursUsed = this.recordHoursUsed.add(periodHoursUsed);
        nextPeriodAllowanceUsage.recordMoneyUsed = this.recordMoneyUsed.add(periodMoneyUsed);
        return nextPeriodAllowanceUsage;
    }

    /* --- Getters / Setters --- */

    public PayPeriod getPayPeriod() {
        return payPeriod;
    }

    public BigDecimal getPeriodHoursUsed() {
        return periodHoursUsed;
    }

    public void setPeriodHoursUsed(BigDecimal periodHoursUsed) {
        this.periodHoursUsed = periodHoursUsed;
    }

    public BigDecimal getPeriodMoneyUsed() {
        return periodMoneyUsed;
    }

    public void setPeriodMoneyUsed(BigDecimal periodMoneyUsed) {
        this.periodMoneyUsed = periodMoneyUsed;
    }
}
