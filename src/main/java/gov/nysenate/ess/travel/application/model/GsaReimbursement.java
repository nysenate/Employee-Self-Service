package gov.nysenate.ess.travel.application.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class GsaReimbursement {

    private BigDecimal meals;
    private BigDecimal lodging;
    private BigDecimal incidental;

    public GsaReimbursement(String meals, String lodging, String incidental) {
        this.meals = round(new BigDecimal(meals));
        this.lodging = round(new BigDecimal(lodging));
        this.incidental = round(new BigDecimal(incidental));
    }

    public GsaReimbursement(BigDecimal meals, BigDecimal lodging,
                            BigDecimal incidental) {
        this.meals = round(meals);
        this.lodging = round(lodging);
        this.incidental = round(incidental);
    }

    public BigDecimal total() {
        return meals.add(lodging).add(incidental);
    }

    public BigDecimal getMeals() {
        return meals;
    }

    public BigDecimal getLodging() {
        return lodging;
    }

    public BigDecimal getIncidental() {
        return incidental;
    }

    /**
     * Round a big decimal to 2 decimal digits using the rounding mode
     * for monetary transactions.
     */
    private BigDecimal round(BigDecimal d) {
        return d.setScale(2, RoundingMode.HALF_UP);
    }
}
