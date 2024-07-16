package gov.nysenate.ess.travel.request.allowances.meal;

import gov.nysenate.ess.travel.utils.Dollars;

import java.util.Objects;

public class MealPerDiemAdjustments {
    private final Dollars overrideRate;
    private final boolean isAllowedMeals;

    private MealPerDiemAdjustments(Builder builder) {
        this.overrideRate = builder.overrideRate;
        this.isAllowedMeals = builder.isAllowedMeals;
    }

    protected boolean isOverridden() {
        return !overrideRate.equals(Dollars.ZERO);
    }

    protected Dollars overrideRate() {
        return this.overrideRate;
    }

    protected boolean isAllowedMeals() {
        return this.isAllowedMeals;
    }

    @Override
    public String toString() {
        return "MealPerDiemAdjustments{" +
                "overrideRate=" + overrideRate +
                ", isAllowedMeals=" + isAllowedMeals +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MealPerDiemAdjustments that = (MealPerDiemAdjustments) o;
        return isAllowedMeals == that.isAllowedMeals
                && Objects.equals(overrideRate, that.overrideRate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(overrideRate, isAllowedMeals);
    }

    // Use a builder to define default values.
    protected static class Builder {
        private Dollars overrideRate = Dollars.ZERO;
        private boolean isAllowedMeals = false;

        public Builder withOverrideRate(Dollars overrideRate) {
            this.overrideRate = overrideRate == null ? Dollars.ZERO : overrideRate;
            return this;
        }

        public Builder withIsAllowedMeals(boolean allowedMeals) {
            isAllowedMeals = allowedMeals;
            return this;
        }

        public MealPerDiemAdjustments build() {
            return new MealPerDiemAdjustments(this);
        }
    }
}
