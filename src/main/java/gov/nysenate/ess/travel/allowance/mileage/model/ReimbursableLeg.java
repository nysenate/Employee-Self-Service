package gov.nysenate.ess.travel.allowance.mileage.model;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Contains the driving distance for a single leg of a trip.
 */
public class ReimbursableLeg {

    private final Leg leg;
    /** The driving distance of this Leg in miles. */
    private final BigDecimal distance;

    public ReimbursableLeg(Leg leg, BigDecimal distance) {
        this.leg = leg;
        this.distance = distance;
    }

    public Leg getLeg() {
        return leg;
    }

    /**
     * Returns the driving distance in miles.
     * @return
     */
    public BigDecimal getDistance() {
        return distance;
    }

    @Override
    public String toString() {
        return "ReimbursableLeg{" +
                "leg=" + leg +
                ", distance=" + distance +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReimbursableLeg that = (ReimbursableLeg) o;
        return Objects.equals(leg, that.leg) &&
                Objects.equals(distance, that.distance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(leg, distance);
    }
}
