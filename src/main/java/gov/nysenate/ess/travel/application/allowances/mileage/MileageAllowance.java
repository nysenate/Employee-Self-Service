package gov.nysenate.ess.travel.application.allowances.mileage;

import gov.nysenate.ess.travel.application.route.Leg;
import gov.nysenate.ess.travel.utils.Dollars;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

public class MileageAllowance {

    private final UUID id;
    private final Leg leg;
    private final double miles;
    private final BigDecimal mileageRate;

    public MileageAllowance(UUID id, Leg leg, double miles, BigDecimal mileageRate) {
        this.id = id;
        this.leg = leg;
        this.miles = miles;
        this.mileageRate = mileageRate;
    }

    public Dollars allowance() {
        return new Dollars(mileageRate.multiply(new BigDecimal(miles)));
    }

    public double getMiles() {
        return this.miles;
    }

    protected UUID getId() {
        return id;
    }

    protected Leg getLeg() {
        return this.leg;
    }

    protected BigDecimal getMileageRate() {
        return this.mileageRate;
    }

    @Override
    public String toString() {
        return "MileageAllowance{" +
                "id=" + id +
                ", leg=" + leg +
                ", miles=" + miles +
                ", mileageRate=" + mileageRate +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MileageAllowance that = (MileageAllowance) o;
        return Double.compare(that.miles, miles) == 0 &&
                Objects.equals(leg, that.leg) &&
                Objects.equals(mileageRate, that.mileageRate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(leg, miles, mileageRate);
    }
}
