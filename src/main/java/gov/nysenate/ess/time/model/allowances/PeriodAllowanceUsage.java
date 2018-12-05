package gov.nysenate.ess.time.model.allowances;

import gov.nysenate.ess.core.model.period.PayPeriod;

import java.math.BigDecimal;

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

    public PeriodAllowanceUsage(PayPeriod payPeriod, AllowanceUsage priorUsage, AllowanceUsage periodUsage) {
        super(priorUsage);
        this.payPeriod = payPeriod;
        this.periodHoursUsed = periodUsage.getHoursUsed();
        this.periodMoneyUsed = periodUsage.getMoneyUsed();
        this.toDate = payPeriod.getEndDate().plusDays(1);
    }

    /* --- Getters / Setters --- */

    public PayPeriod getPayPeriod() {
        return payPeriod;
    }

    public BigDecimal getPeriodHoursUsed() {
        return periodHoursUsed;
    }

    public BigDecimal getPeriodMoneyUsed() {
        return periodMoneyUsed;
    }
}
