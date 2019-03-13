package gov.nysenate.ess.travel.application.route;

import gov.nysenate.ess.core.model.unit.Address;
import gov.nysenate.ess.travel.utils.Dollars;

import java.time.LocalDate;

public final class PerDiem {

    private final Address address;
    private final LocalDate date;
    private final Dollars dollars;

    public PerDiem(Address address, LocalDate date, Dollars dollars) {
        this.address = address;
        this.date = date;
        this.dollars = dollars;
    }

    public Address getAddress() {
        return address;
    }

    public LocalDate getDate() {
        return date;
    }

    public Dollars getDollars() {
        return dollars;
    }
}
