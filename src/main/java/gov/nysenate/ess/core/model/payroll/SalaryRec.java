package gov.nysenate.ess.core.model.payroll;

import com.google.common.collect.Range;
import gov.nysenate.ess.core.util.DateUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class SalaryRec implements Comparable<SalaryRec>
{
    /** The amount paid to the employee per pay period/hour/year */
    private BigDecimal salaryRate;

    /** Specifies the time unit that this salary is tracked by e.g. biweekly/hourly/yearly */
    private PayType payType;

    /** The start and end points for the range of dates for which this salary is in effect */
    private LocalDate effectDate;
    private LocalDate endDate = DateUtils.THE_FUTURE;

    /** The date this record was created */
    private LocalDateTime auditDate;

    public SalaryRec(BigDecimal salaryRate, PayType payType, LocalDate effectDate, LocalDateTime auditDate) {
        this.salaryRate = salaryRate;
        this.payType = payType;
        this.effectDate = effectDate;
        this.auditDate = auditDate;
    }

    /* --- Overridden Methods --- */

    @Override
    public int compareTo(SalaryRec o) {
        // Compare based on salary rate
        return this.getSalaryRate().compareTo(o.getSalaryRate());
    }

    /* --- Functional Getters --- */

    public Range<LocalDate> getEffectiveRange() {
        return Range.closedOpen(effectDate, endDate.plusDays(1));
    }


    /* --- Getters / Setters --- */

    public LocalDateTime getAuditDate() {
        return auditDate;
    }

    public BigDecimal getSalaryRate() {
        return salaryRate;
    }

    public PayType getPayType() {
        return payType;
    }

    public LocalDate getEffectDate() {
        return effectDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
}
