package gov.nysenate.ess.travel.application.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class TravelDay {

    private LocalDate currentDate;
    private BigDecimal breakfastCost;
    private BigDecimal dinnerCost;
    private BigDecimal lodging;

    public TravelDay(LocalDate currentDate, double breakfastCost, double dinnerCost) {
        this.currentDate = currentDate;
        this.breakfastCost = new BigDecimal(breakfastCost);
        this.dinnerCost = new BigDecimal(dinnerCost);
        lodging = BigDecimal.ZERO;
    }

    public void setLodging(double lodging) {
        this.lodging = new BigDecimal(lodging);
    }

    public BigDecimal getBreakfastCost() {
        return breakfastCost;
    }

    public BigDecimal getDinnerCost() {
        return dinnerCost;
    }

    public BigDecimal getLodging() {
        return lodging;
    }
}
