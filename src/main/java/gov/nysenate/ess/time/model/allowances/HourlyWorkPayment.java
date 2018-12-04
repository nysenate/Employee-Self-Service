package gov.nysenate.ess.time.model.allowances;

import com.google.common.collect.Range;
import gov.nysenate.ess.core.util.DateUtils;
import gov.nysenate.ess.core.util.RangeUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * A class that models a payment transaction for an hourly temporary employee
 */
public class HourlyWorkPayment {

    /** Indicates the start of the work period that this payment is compensating for */
    private LocalDate effectDate;
    /** The last day of the work period that this payment is compensating for */
    private LocalDate endDate;
    private LocalDateTime auditDate;

    private BigDecimal hoursPaid;

    /** Total money paid */
    private BigDecimal moneyPaid;

    /** Money paid for the year prior to the year of endDate */
    private BigDecimal prevYearMoneyPaid;

    public HourlyWorkPayment(LocalDateTime auditDate, LocalDate effectDate, LocalDate endDate,
                             BigDecimal hoursPaid, BigDecimal moneyPaid, BigDecimal prevYearMoneyPaid) {
        this.auditDate = auditDate;
        this.effectDate = effectDate;
        this.endDate = endDate;
        this.hoursPaid = hoursPaid;
        this.moneyPaid = moneyPaid;
        this.prevYearMoneyPaid = Optional.ofNullable(prevYearMoneyPaid).orElse(BigDecimal.ZERO);
    }

    /** --- Overridden Methods --- */

    @Override
    public String toString() {
        return effectDate + " - " + endDate + " " + hoursPaid + "hrs $" + moneyPaid;
    }

    /** --- Functional Getters / Setters */

    public int getYear() {
        return endDate.getYear();
    }

    /**
     * Return the amount of money from this transaction that was paid for the given year
     */
    public BigDecimal getMoneyPaidForYear(int year) {
        if (year == endDate.getYear()) {
            return moneyPaid.subtract(prevYearMoneyPaid);
        } else if (year == effectDate.getYear()) {
            return prevYearMoneyPaid;
        }
        return BigDecimal.ZERO;
    }

    /** Return the range of work dates that this payment is compensating for.*/
    public Range<LocalDate> getWorkingRange() {
        return Range.closedOpen(effectDate, endDate.plusDays(1));
    }

    /** Return the range of work days in this payment for the given year. */
    public Range<LocalDate> getWorkingRangeForYear(int year) {
        Range<LocalDate> yearDateRange = DateUtils.yearDateRange(year);
        Range<LocalDate> workingRange = getWorkingRange();
        if (RangeUtils.intersects(workingRange, yearDateRange)) {
            return workingRange.intersection(yearDateRange);
        }
        return null;
    }

    /** --- Getters / Setters --- */

    public LocalDate getEffectDate() {
        return effectDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public LocalDateTime getAuditDate() {
        return auditDate;
    }

    public BigDecimal getHoursPaid() {
        return hoursPaid;
    }

    public BigDecimal getMoneyPaid() {
        return moneyPaid;
    }

    public BigDecimal getPrevYearMoneyPaid() {
        return prevYearMoneyPaid;
    }
}
