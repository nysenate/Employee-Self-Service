package gov.nysenate.ess.travel.application.overrides.perdiem;

import gov.nysenate.ess.travel.application.TravelApplication;
import gov.nysenate.ess.travel.application.route.Route;
import gov.nysenate.ess.travel.utils.Dollars;

import java.util.EnumSet;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * PerDiemOverrides can be set by reviewers when editing a {@link TravelApplication}.
 * If set, and not $0, they override the equivalent perdiem from the {@link Route}.
 *
 * i.e. Setting mileageOverride to $20 will cause the mileage perdiem in Route to be
 * ignored. The total mileage allowance for the application would be $20.
 */
public class PerDiemOverrides {

    Map<PerDiemType, PerDiemOverride> typeToOverride;

    public PerDiemOverrides() {
        // Initialize all overrides to $0
        this.typeToOverride = EnumSet.allOf(PerDiemType.class).stream()
                .collect(Collectors.toMap(Function.identity(), type -> new PerDiemOverride(type, Dollars.ZERO)));
    }

    public boolean isMileageOverridden() {
        return isGreaterThanZero(mileageOverride());
    }

    public Dollars mileageOverride() {
        return typeToOverride.get(PerDiemType.MILEAGE).dollars;
    }

    /**
     * Set the mileage perdiem override value.
     * @param dollars The value to set the mileage override. Cannot be negative.
     */
    public void setMileageOverride(Dollars dollars) {
        guardAgainstNegatives(dollars);
        typeToOverride.get(PerDiemType.MILEAGE).dollars = dollars;
    }

    public boolean isMealsOverridden() {
        return isGreaterThanZero(mealsOverride());
    }

    public Dollars mealsOverride() {
        return typeToOverride.get(PerDiemType.MEALS).dollars;
    }

    /**
     * Sets the meals perdiem override
     * @param dollars The value to set the meals override. Cannot be negative.
     */
    public void setMealsOverride(Dollars dollars) {
        guardAgainstNegatives(dollars);
        typeToOverride.get(PerDiemType.MEALS).dollars = dollars;
    }

    public boolean isLodgingOverridden() {
        return isGreaterThanZero(lodgingOverride());
    }

    public Dollars lodgingOverride() {
        return typeToOverride.get(PerDiemType.LODGING).dollars;
    }

    /**
     * Sets the lodging perdiem override.
     * @param dollars The value to set the lodging override. Cannot be negative.
     */
    public void setLodgingOverride(Dollars dollars) {
        guardAgainstNegatives(dollars);
        typeToOverride.get(PerDiemType.LODGING).dollars = dollars;
    }

    private boolean isGreaterThanZero(Dollars dollars) {
        return dollars.compareTo(Dollars.ZERO) > 0;
    }

    private void guardAgainstNegatives(Dollars dollars) {
        if (dollars.compareTo(Dollars.ZERO) < 0) {
            throw new IllegalArgumentException("PerDiem Overrides cannot be set to a negative value.");
        }
    }

    @Override
    public String toString() {
        return "PerDiemOverrides{" +
                "typeToOverride=" + typeToOverride +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PerDiemOverrides that = (PerDiemOverrides) o;
        return Objects.equals(typeToOverride, that.typeToOverride);
    }

    @Override
    public int hashCode() {
        return Objects.hash(typeToOverride);
    }
}
