package gov.nysenate.ess.travel;

import gov.nysenate.ess.travel.route.Leg;
import gov.nysenate.ess.travel.utils.Dollars;

import java.math.BigDecimal;

public class MileageAllowance {

    private final Leg leg;
    private final double miles;
    private final BigDecimal mileageRate;

    public MileageAllowance(Leg leg, double miles, BigDecimal mileageRate) {
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

    protected Leg getLeg() {
        return this.leg;
    }

    protected BigDecimal getMileageRate() {
        return this.mileageRate;
    }
}
