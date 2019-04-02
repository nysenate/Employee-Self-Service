package gov.nysenate.ess.travel.application.route;

import gov.nysenate.ess.travel.application.route.destination.Destination;
import gov.nysenate.ess.travel.utils.Dollars;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

public class Leg {

    private int id;
    private final Destination from;
    private final Destination to;
    private final ModeOfTransportation modeOfTransportation;
    private final LocalDate travelDate;
    private final double miles;
    private final BigDecimal mileageRate;

    public Leg(int id, Destination from, Destination to, ModeOfTransportation modeOfTransportation,
               LocalDate travelDate, double miles, BigDecimal mileageRate) {
        this.id = id;
        this.from = Objects.requireNonNull(from);
        this.to = Objects.requireNonNull(to);
        this.modeOfTransportation = Objects.requireNonNull(modeOfTransportation);
        this.travelDate = Objects.requireNonNull(travelDate);
        this.miles = miles;
        this.mileageRate = mileageRate;
    }

    public int getId() {
        return id;
    }

    public Destination getFrom() {
        return from;
    }

    public Destination getTo() {
        return to;
    }

    public LocalDate getTravelDate() {
        return travelDate;
    }

    public Dollars mileageExpense() {
        if (qualifiesForMileageReimbursement()) {
            return new Dollars(getMileageRate().multiply(new BigDecimal(miles)));
        }
        return Dollars.ZERO;
    }

    public double getMiles() {
        return miles;
    }

    public BigDecimal getMileageRate() {
        return mileageRate;
    }

    ModeOfTransportation getModeOfTransportation() {
        return modeOfTransportation;
    }

    boolean qualifiesForMileageReimbursement() {
        return getModeOfTransportation().qualifiesForMileageReimbursement();
    }

    void setId(int id) {
        this.id = id;
    }


    @Override
    public String toString() {
        return "Leg{" +
                "id=" + id +
                ", from=" + from +
                ", to=" + to +
                ", modeOfTransportation=" + modeOfTransportation +
                ", travelDate=" + travelDate +
                ", miles=" + miles +
                ", mileageRate=" + mileageRate +
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
                Objects.equals(modeOfTransportation, leg.modeOfTransportation) &&
                Objects.equals(travelDate, leg.travelDate) &&
                Objects.equals(mileageRate, leg.mileageRate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to, modeOfTransportation, travelDate, miles, mileageRate);
    }
}
