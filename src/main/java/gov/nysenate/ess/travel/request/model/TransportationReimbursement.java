package gov.nysenate.ess.travel.request.model;

import java.math.BigDecimal;

public class TransportationReimbursement {

    private BigDecimal milage;
    private BigDecimal tolls;
    // TODO How to handle other travel types
    // Parking
    // Train, Bus, Taxi, Airplane...


    public TransportationReimbursement(BigDecimal milage, BigDecimal tolls) {
        this.milage = milage;
        this.tolls = tolls;
    }

    public BigDecimal getMilage() {
        return milage;
    }

    public BigDecimal getTolls() {
        return tolls;
    }
}
