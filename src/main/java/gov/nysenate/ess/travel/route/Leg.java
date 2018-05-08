package gov.nysenate.ess.travel.route;

import gov.nysenate.ess.core.model.unit.Address;

import java.time.LocalDate;
import java.util.EnumSet;
import java.util.Objects;

public class Leg {

    private final Address from;
    private final Address to;
    private final double miles;
    private final ModeOfTransportation modeOfTransportation;
    private final LocalDate travelDate;
    private final boolean isMileageRequested;

    public Leg(Address from, Address to, double miles, ModeOfTransportation modeOfTransportation,
               LocalDate travelDate, boolean isMileageRequested) {
        this.from = Objects.requireNonNull(from);
        this.to = Objects.requireNonNull(to);
        this.miles = miles;
        this.modeOfTransportation = Objects.requireNonNull(modeOfTransportation);
        this.travelDate = Objects.requireNonNull(travelDate);
        this.isMileageRequested = isMileageRequested;
    }

    /**
     * Does this Leg qualify for Mileage Reimbursement.
     */
    boolean qualifies() {
        return getModeOfTransportation().qualifiesForMileageReimbursement()
                && isMileageRequested();
    }

    public Address getFrom() {
        return from;
    }

    public Address getTo() {
        return to;
    }

    public LocalDate getTravelDate() {
        return travelDate;
    }

    double getMiles() {
        return miles;
    }

    ModeOfTransportation getModeOfTransportation() {
        return modeOfTransportation;
    }

    boolean isMileageRequested() {
        return isMileageRequested;
    }

    @Override
    public String toString() {
        return "Leg{" +
                "from=" + from +
                ", to=" + to +
                ", miles=" + miles +
                ", modeOfTransportation=" + modeOfTransportation +
                ", travelDate=" + travelDate +
                ", isMileageRequested=" + isMileageRequested +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Leg leg = (Leg) o;
        return Double.compare(leg.miles, miles) == 0 &&
                isMileageRequested == leg.isMileageRequested &&
                Objects.equals(from, leg.from) &&
                Objects.equals(to, leg.to) &&
                modeOfTransportation == leg.modeOfTransportation &&
                Objects.equals(travelDate, leg.travelDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to, miles, modeOfTransportation, travelDate, isMileageRequested);
    }
}
