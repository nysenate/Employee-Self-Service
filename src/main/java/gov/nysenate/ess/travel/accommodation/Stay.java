package gov.nysenate.ess.travel.accommodation;

import gov.nysenate.ess.travel.utils.Dollars;

import java.time.LocalDate;
import java.util.Objects;

public abstract class Stay {

    private final LocalDate date;

    public Stay(LocalDate date) {
        this.date = date;
    }

    public Dollars lodgingAllowance() {
        return Dollars.ZERO;
    }

    public Dollars mealAllowance() {
        return Dollars.ZERO;
    }

    protected LocalDate getDate() {
        return this.date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Stay stay = (Stay) o;
        return Objects.equals(date, stay.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date);
    }
}
