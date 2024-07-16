package gov.nysenate.ess.travel.request.allowances;

import gov.nysenate.ess.travel.utils.Dollars;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

public final class PerDiem implements Comparable<PerDiem> {

    private final LocalDate date;
    private final BigDecimal rate;

    public PerDiem(LocalDate date, Dollars rate) {
        this(date, new BigDecimal(rate.toString()));
    }

    public PerDiem(LocalDate date, BigDecimal rate) {
        this.date = date;
        this.rate = rate;
    }

    /**
     * Checks if the rate for this Per Diem is zero.
     * Uses compareTo instead of equals to check only the value for equality, not the scale.
     * @return
     */
    public boolean isRateZero() {
        return rate.compareTo(new BigDecimal("0")) == 0;
    }

    public LocalDate getDate() {
        return date;
    }

    public BigDecimal getRate() {
        return rate;
    }

    @Override
    public String toString() {
        return "PerDiem{" +
                "date=" + date +
                ", rate=" + rate +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PerDiem perDiem = (PerDiem) o;
        return Objects.equals(date, perDiem.date) &&
                Objects.equals(rate, perDiem.rate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, rate);
    }

    @Override
    public int compareTo(PerDiem o) {
        if (this.date.equals(o.date)) {
            return this.rate.compareTo(o.rate);
        }
        else {
            return this.date.compareTo(o.date);
        }
    }
}
