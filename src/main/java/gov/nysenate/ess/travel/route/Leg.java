package gov.nysenate.ess.travel.route;

import gov.nysenate.ess.core.model.unit.Address;

import java.util.EnumSet;
import java.util.Objects;

public class Leg {

    private static final EnumSet<ModeOfTransportation> QUALIFYING_MOT =
            EnumSet.of(ModeOfTransportation.PERSONAL_AUTO);

    private final Address from;
    private final Address to;
    private final double miles;
    private final ModeOfTransportation modeOfTransportation;

    public Leg(Address from, Address to, double miles, ModeOfTransportation modeOfTransportation) {
        this.from = Objects.requireNonNull(from);
        this.to = Objects.requireNonNull(to);
        this.miles = miles;
        this.modeOfTransportation = Objects.requireNonNull(modeOfTransportation);
    }

    /**
     * Does this Leg qualify for Mileage Reimbursement.
     */
    boolean qualifies() {
        return QUALIFYING_MOT.contains(getModeOfTransportation());
    }

    Address getFrom() {
        return from;
    }

    Address getTo() {
        return to;
    }

    double getMiles() {
        return miles;
    }

    ModeOfTransportation getModeOfTransportation() {
        return modeOfTransportation;
    }

    @Override
    public String toString() {
        return "Leg{" +
                "from=" + from +
                ", to=" + to +
                ", miles=" + miles +
                ", modeOfTransportation=" + modeOfTransportation +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Leg leg = (Leg) o;
        return Double.compare(leg.miles, miles) == 0 &&
                Objects.equals(from, leg.from) &&
                Objects.equals(to, leg.to) &&
                modeOfTransportation == leg.modeOfTransportation;
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to, miles, modeOfTransportation);
    }
}
