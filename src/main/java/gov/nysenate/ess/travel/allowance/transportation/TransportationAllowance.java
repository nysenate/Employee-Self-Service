package gov.nysenate.ess.travel.allowance.transportation;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class TransportationAllowance {

    private BigDecimal mileage;
    private BigDecimal tolls;
    // TODO How to handle other travel types
    // Parking
    // Train, Bus, Taxi, Airplane...


    public TransportationAllowance(String mileage, String tolls) {
        this.mileage = round(new BigDecimal(mileage));
        this.tolls = round(new BigDecimal(tolls));
    }

    public TransportationAllowance(BigDecimal mileage, BigDecimal tolls) {
        this.mileage = round(mileage);
        this.tolls = round(tolls);
    }

    public BigDecimal total() {
        return mileage.add(tolls);
    }

    public BigDecimal getMileage() {
        return mileage;
    }

    public BigDecimal getTolls() {
        return tolls;
    }

    /**
     * Round to 2 decimal digits using the rounding mode for monetary transactions.
     */
    private BigDecimal round(BigDecimal d) {
        return d.setScale(2, RoundingMode.HALF_UP);
    }
}
