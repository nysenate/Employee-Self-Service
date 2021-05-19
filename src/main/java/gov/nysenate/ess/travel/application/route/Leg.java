package gov.nysenate.ess.travel.application.route;

import gov.nysenate.ess.travel.application.address.TravelAddress;
import gov.nysenate.ess.travel.application.allowances.PerDiem;
import gov.nysenate.ess.travel.application.route.destination.Destination;
import gov.nysenate.ess.travel.utils.Dollars;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

public class Leg {

    private int id;
    private Destination from;
    private Destination to;
    private final ModeOfTransportation modeOfTransportation;
    private final double miles;
    private final PerDiem mileagePerDiem;
    private final boolean isOutbound;
    private boolean isReimbursementRequested;

    public Leg(int id, Destination from, Destination to, ModeOfTransportation modeOfTransportation,
               double miles, PerDiem mileagePerDiem, boolean isOutbound, boolean isReimbursementRequested) {
        this.id = id;
        this.from = Objects.requireNonNull(from);
        this.to = Objects.requireNonNull(to);
        this.modeOfTransportation = Objects.requireNonNull(modeOfTransportation);
        this.miles = miles;
        this.mileagePerDiem = mileagePerDiem;
        this.isOutbound = isOutbound;
        this.isReimbursementRequested = isReimbursementRequested;
    }

    /**
     * The maximum mileage allowance allowed for this leg of the trip.
     *
     * @return
     */
    public Dollars maximumPerDiem() {
        return qualifiesForMileageReimbursement()
                ? new Dollars(mileageRate().multiply(new BigDecimal(miles)))
                : Dollars.ZERO;
    }

    /**
     * The mileage allowance requested and allowed for this leg of the trip.
     *
     * @return
     */
    public Dollars requestedPerDiem() {
        return isReimbursementRequested()
                ? maximumPerDiem()
                : Dollars.ZERO;
    }

    public void setIsReimbursementRequested(boolean isReimbursementRequested) {
        this.isReimbursementRequested = isReimbursementRequested;
    }

    public void setFromDestination(Destination from) {
        this.from = from;
    }

    public void setToDestination(Destination to) {
        this.to = to;
    }

    public int getId() {
        return id;
    }

    public TravelAddress fromAddress() {
        return from.getAddress();
    }

    public TravelAddress toAddress() {
        return to.getAddress();
    }

    public Destination from() {
        return from;
    }

    public Destination to() {
        return to;
    }

    public LocalDate travelDate() {
        return mileagePerDiem.getDate();
    }

    public double miles() {
        return miles;
    }

    public BigDecimal mileageRate() {
        return mileagePerDiem.getRate();
    }

    public String methodOfTravel() {
        return modeOfTransportation.getMethodOfTravel().name();
    }

    public String methodOfTravelDisplayName() {
        return modeOfTransportation.getMethodOfTravel().getDisplayName();
    }

    public String methodOfTravelDescription() {
        return modeOfTransportation.getDescription();
    }

    public boolean isReimbursementRequested() {
        return this.isReimbursementRequested;
    }

    public boolean isOutbound() {
        return isOutbound;
    }

    public boolean qualifiesForMileageReimbursement() {
        return modeOfTransportation.qualifiesForMileageReimbursement();
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
                ", miles=" + miles +
                ", perDiem=" + mileagePerDiem +
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
                Objects.equals(mileagePerDiem, leg.mileagePerDiem);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, from, to, modeOfTransportation, miles, mileagePerDiem, isOutbound);
    }
}

