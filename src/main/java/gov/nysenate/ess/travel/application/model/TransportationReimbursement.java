package gov.nysenate.ess.travel.application.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Locale;

public class TransportationReimbursement {

    private BigDecimal milage;
    private BigDecimal tolls;
    // TODO How to handle other travel types
    // Parking
    // Train, Bus, Taxi, Airplane...


    public TransportationReimbursement(String milage, String tolls) {
        this.milage = round(new BigDecimal(milage));
        this.tolls = round(new BigDecimal(tolls));
    }

    public TransportationReimbursement(BigDecimal milage, BigDecimal tolls) {
        this.milage = round(milage);
        this.tolls = round(tolls);
    }

    public BigDecimal total() {
        return milage.add(tolls);
    }

    public BigDecimal getMilage() {
        return milage;
    }

    public BigDecimal getTolls() {
        return tolls;
    }

    /**
     * Round a big decimal to 2 decimal digits using the rounding mode
     * for monetary transactions.
     */
    private BigDecimal round(BigDecimal d) {
        return d.setScale(2, RoundingMode.HALF_UP);
    }
}
