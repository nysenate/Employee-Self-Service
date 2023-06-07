package gov.nysenate.ess.travel.request.route;

import gov.nysenate.ess.travel.request.address.TravelAddress;
import gov.nysenate.ess.travel.request.allowances.PerDiem;
import gov.nysenate.ess.travel.request.route.destination.Destination;
import gov.nysenate.ess.travel.utils.Dollars;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

public class Leg {

    private int id;
    private Destination from;
    private Destination to;
    private final ModeOfTransportation modeOfTransportation;
    private LocalDate travelDate;
    private final boolean isOutbound;


    public Leg(int id, Destination from, Destination to, ModeOfTransportation modeOfTransportation,
               boolean isOutbound, LocalDate travelDate) {
        this.id = id;
        this.from = Objects.requireNonNull(from);
        this.to = Objects.requireNonNull(to);
        this.modeOfTransportation = Objects.requireNonNull(modeOfTransportation);
        this.travelDate = travelDate;
        this.isOutbound = isOutbound;
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
        return travelDate;
    }

    public ModeOfTransportation getModeOfTransportation() {
        return modeOfTransportation;
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

    public boolean isOutbound() {
        return isOutbound;
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
                ", isOutbound=" + isOutbound +
                ", travelDate=" + travelDate +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Leg leg = (Leg) o;
        return id == leg.id
                && isOutbound == leg.isOutbound
                && Objects.equals(from, leg.from)
                && Objects.equals(to, leg.to)
                && Objects.equals(modeOfTransportation, leg.modeOfTransportation)
                && Objects.equals(travelDate, leg.travelDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, from, to, modeOfTransportation, isOutbound, travelDate);
    }
}

