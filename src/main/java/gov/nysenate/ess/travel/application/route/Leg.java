package gov.nysenate.ess.travel.application.route;

import gov.nysenate.ess.travel.application.address.TravelAddress;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

public class Leg {

    private final UUID id;
    private final TravelAddress from;
    private final TravelAddress to;
    private final ModeOfTransportation modeOfTransportation;
    private final LocalDate travelDate;

    public Leg(UUID id, TravelAddress from, TravelAddress to, ModeOfTransportation modeOfTransportation, LocalDate travelDate) {
        this.id = id;
        this.from = Objects.requireNonNull(from);
        this.to = Objects.requireNonNull(to);
        this.modeOfTransportation = Objects.requireNonNull(modeOfTransportation);
        this.travelDate = Objects.requireNonNull(travelDate);
    }

    public UUID getId() {
        return id;
    }

    public TravelAddress getFrom() {
        return from;
    }

    public TravelAddress getTo() {
        return to;
    }

    public LocalDate getTravelDate() {
        return travelDate;
    }

    public ModeOfTransportation getModeOfTransportation() {
        return modeOfTransportation;
    }

    @Override
    public String toString() {
        return "Leg{" +
                "from=" + from +
                ", to=" + to +
                ", modeOfTransportation=" + modeOfTransportation +
                ", travelDate=" + travelDate +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Leg leg = (Leg) o;
        return Objects.equals(from, leg.from) &&
                Objects.equals(to, leg.to) &&
                Objects.equals(modeOfTransportation, leg.modeOfTransportation) &&
                Objects.equals(travelDate, leg.travelDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to, modeOfTransportation, travelDate);
    }
}
