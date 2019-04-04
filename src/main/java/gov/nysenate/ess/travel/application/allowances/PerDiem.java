package gov.nysenate.ess.travel.application.allowances;

import gov.nysenate.ess.travel.utils.Dollars;

import java.time.LocalDate;
import java.util.Objects;

public final class PerDiem {

    private final LocalDate date;
    private final Dollars dollars;
    private boolean reimbursementRequested;

    public PerDiem(LocalDate date, Dollars dollars) {
        this(date, dollars, true);
    }

    public PerDiem(LocalDate date, Dollars dollars, boolean reimbursementRequested) {
        this.date = date;
        this.dollars = dollars;
        this.reimbursementRequested = reimbursementRequested;
    }

    public LocalDate getDate() {
        return date;
    }

    public Dollars getDollars() {
        return dollars;
    }

    public boolean isReimbursementRequested() {
        return reimbursementRequested;
    }

    @Override
    public String toString() {
        return "PerDiem{" +
                "date=" + date +
                ", dollars=" + dollars +
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
                Objects.equals(dollars, perDiem.dollars);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, dollars, reimbursementRequested);
    }
}
