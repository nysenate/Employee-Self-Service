package gov.nysenate.ess.travel.request.model;

import java.math.BigDecimal;

public class GsaReimbursement {

    private BigDecimal meals;
    private BigDecimal lodging;
    private BigDecimal incidental;

    public GsaReimbursement(BigDecimal meals, BigDecimal lodging,
                            BigDecimal incidental) {
        this.meals = meals;
        this.lodging = lodging;
        this.incidental = incidental;
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
}
