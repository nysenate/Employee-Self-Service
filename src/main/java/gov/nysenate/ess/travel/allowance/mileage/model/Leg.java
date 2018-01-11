package gov.nysenate.ess.travel.allowance.mileage.model;

import gov.nysenate.ess.core.model.unit.Address;

import java.util.Objects;

/**
 * Represents a single leg of a travel application.
 */
public class Leg {

    private final Address from;
    private final Address to;

    public Leg(Address from, Address to) {
        this.from = from;
        this.to = to;
    }

    public Address getFrom() {
        return from;
    }

    public Address getTo() {
        return to;
    }

    @Override
    public String toString() {
        return "Leg{" +
                "from=" + from +
                ", to=" + to +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Leg leg = (Leg) o;
        return Objects.equals(from, leg.from) &&
                Objects.equals(to, leg.to);
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to);
    }
}
