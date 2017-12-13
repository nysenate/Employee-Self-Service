package gov.nysenate.ess.travel.allowance.gsa.model;

import gov.nysenate.ess.travel.utils.TravelAllowanceUtils;

import java.math.BigDecimal;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public final class GsaAllowance {

    private final BigDecimal meals;
    private final BigDecimal lodging;

    /**
     * Construct a GsaAllowance.<br>
     * GsaAllowance requirements are enforced by the constructor:<br>
     *   - params cannot be null.<br>
     *   - params must evaluate to a {@code BigDecimal} > 0.<br>
     *   - {@code BigDecimals} are rounded to the nearest 2 decimal places.<br>
     */
    public GsaAllowance(BigDecimal meals, BigDecimal lodging) {
        checkArgument(checkNotNull(meals).signum() >= 0);
        checkArgument(checkNotNull(lodging).signum() >= 0);
        this.meals = TravelAllowanceUtils.round(meals);
        this.lodging = TravelAllowanceUtils.round(lodging);
    }

    public GsaAllowance(String meals, String lodging) {
        this(new BigDecimal(meals), new BigDecimal(lodging));
    }

    public BigDecimal total() {
        return getMeals().add(getLodging());
    }

    public BigDecimal getMeals() {
        return meals;
    }

    public BigDecimal getLodging() {
        return lodging;
    }

    @Override
    public String toString() {
        return "GsaAllowance{" +
                "meals=" + meals +
                ", lodging=" + lodging +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GsaAllowance that = (GsaAllowance) o;

        if (meals != null ? !meals.equals(that.meals) : that.meals != null) return false;
        return lodging != null ? lodging.equals(that.lodging) : that.lodging == null;
    }

    @Override
    public int hashCode() {
        int result = meals != null ? meals.hashCode() : 0;
        result = 31 * result + (lodging != null ? lodging.hashCode() : 0);
        return result;
    }
}
