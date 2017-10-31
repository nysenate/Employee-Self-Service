package gov.nysenate.ess.travel.allowance.gsa.model;

import gov.nysenate.ess.travel.utils.TravelAllowanceUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class GsaAllowance {

    private final BigDecimal meals;
    private final BigDecimal lodging;
    private final BigDecimal incidental;

    public GsaAllowance(String meals, String lodging, String incidental) {
        this.meals = TravelAllowanceUtils.round(new BigDecimal(meals));
        this.lodging = TravelAllowanceUtils.round(new BigDecimal(lodging));
        this.incidental = TravelAllowanceUtils.round(new BigDecimal(incidental));
    }

    public GsaAllowance(BigDecimal meals, BigDecimal lodging,
                        BigDecimal incidental) {
        this.meals = TravelAllowanceUtils.round(meals);
        this.lodging = TravelAllowanceUtils.round(lodging);
        this.incidental = TravelAllowanceUtils.round(incidental);
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

    @Override
    public String toString() {
        return "GsaAllowance{" +
                "meals=" + meals +
                ", lodging=" + lodging +
                ", incidental=" + incidental +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GsaAllowance that = (GsaAllowance) o;

        if (meals != null ? !meals.equals(that.meals) : that.meals != null) return false;
        if (lodging != null ? !lodging.equals(that.lodging) : that.lodging != null) return false;
        return incidental != null ? incidental.equals(that.incidental) : that.incidental == null;
    }

    @Override
    public int hashCode() {
        int result = meals != null ? meals.hashCode() : 0;
        result = 31 * result + (lodging != null ? lodging.hashCode() : 0);
        result = 31 * result + (incidental != null ? incidental.hashCode() : 0);
        return result;
    }
}
