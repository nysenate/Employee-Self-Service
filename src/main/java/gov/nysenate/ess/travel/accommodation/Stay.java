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
}
