package gov.nysenate.ess.travel.application.allowances;

import gov.nysenate.ess.travel.utils.Dollars;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

public final class PerDiem {

    private final LocalDate date;
    private final BigDecimal rate;
    private boolean reimbursementRequested;

    public PerDiem(LocalDate date, Dollars rate, boolean reimbursementRequested) {
        this(date, new BigDecimal(rate.toString()), reimbursementRequested);
    }

    public PerDiem(LocalDate date, BigDecimal rate, boolean reimbursementRequested) {
        this.date = date;
        this.rate = rate;
        this.reimbursementRequested = reimbursementRequested;
    }

    public LocalDate getDate() {
        return date;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public boolean isReimbursementRequested() {
        return reimbursementRequested;
    }

    @Override
    public String toString() {
        return "PerDiem{" +
                "date=" + date +
                ", rate=" + rate +
                ", reimbursementRequested=" + reimbursementRequested +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PerDiem perDiem = (PerDiem) o;
        return reimbursementRequested == perDiem.reimbursementRequested &&
                Objects.equals(date, perDiem.date) &&
                Objects.equals(rate, perDiem.rate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, rate, reimbursementRequested);
    }
}
