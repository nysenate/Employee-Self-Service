package gov.nysenate.ess.travel.application.route;

import gov.nysenate.ess.core.model.unit.Address;
import gov.nysenate.ess.travel.application.allowances.PerDiem;
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
    private final double miles;
    private final PerDiem perDiem;
    private final boolean isOutbound;

    public Leg(int id, Destination from, Destination to, ModeOfTransportation modeOfTransportation,
               double miles, PerDiem perDiem, boolean isOutbound) {
        this.id = id;
        this.from = Objects.requireNonNull(from);
        this.to = Objects.requireNonNull(to);
        this.modeOfTransportation = Objects.requireNonNull(modeOfTransportation);
        this.miles = miles;
        this.perDiem = perDiem;
        this.isOutbound = isOutbound;
    }

    /**
     * The maximum mileage allowance allowed for this leg of the trip.
     *
     * @return
     */
    public Dollars maximumAllowance() {
        return qualifiesForMileageReimbursement()
                ? new Dollars(mileageRate().multiply(new BigDecimal(miles)))
                : Dollars.ZERO;
    }

    /**
     * The mileage allowance requested and allowed for this leg of the trip.
     *
     * @return
     */
    public Dollars requestedAllowance() {
        return isReimbursementRequested()
                ? maximumAllowance()
                : Dollars.ZERO;
    }

    public int getId() {
        return id;
    }

    public Address fromAddress() {
        return from.getAddress();
    }

    public Address toAddress() {
        return to.getAddress();
    }

    public Destination getFrom() {
        return from;
    }

    public Destination getTo() {
        return to;
    }

    public LocalDate travelDate() {
        return perDiem.getDate();
    }

    public double miles() {
        return miles;
    }

    public BigDecimal mileageRate() {
        return perDiem.getRate();
    }

    public String methodOfTravel() {
        return modeOfTransportation.getMethodOfTravel().name();
    }

    public String methodOfTravelDescription() {
        return modeOfTransportation.getDescription();
    }

    public boolean isReimbursementRequested() {
        return perDiem.isReimbursementRequested();
    }

    public boolean isOutbound() {
        return isOutbound;
    }

    void setId(int id) {
        this.id = id;
    }

    // A leg qualifies for mileage reimbursement if its mode of transportation is personal auto.
    private boolean qualifiesForMileageReimbursement() {
        return modeOfTransportation.qualifiesForMileageReimbursement();
    }

    @Override
    public String toString() {
        return "Leg{" +
                "id=" + id +
                ", from=" + from +
                ", to=" + to +
                ", modeOfTransportation=" + modeOfTransportation +
                ", miles=" + miles +
                ", perDiem=" + perDiem +
                ", isOutbound=" + isOutbound +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Leg leg = (Leg) o;
        return id == leg.id &&
                Double.compare(leg.miles, miles) == 0 &&
                isOutbound == leg.isOutbound &&
                Objects.equals(from, leg.from) &&
                Objects.equals(to, leg.to) &&
                Objects.equals(modeOfTransportation, leg.modeOfTransportation) &&
                Objects.equals(perDiem, leg.perDiem);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, from, to, modeOfTransportation, miles, perDiem, isOutbound);
    }
}

