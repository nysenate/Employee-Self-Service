package gov.nysenate.ess.travel.application.overrides.perdiem;

import com.fasterxml.jackson.annotation.JsonProperty;
import gov.nysenate.ess.core.client.view.base.ViewObject;

public class PerDiemOverridesView implements ViewObject {

    @JsonProperty("isMileageOverridden")
    boolean isMileageOverridden;
    @JsonProperty("isMealsOverridden")
    boolean isMealsOverridden;
    @JsonProperty("isLodgingOverridden")
    boolean isLodgingOverridden;

    double mileageOverride;
    double mealsOverride;
    double lodgingOverride;

    public PerDiemOverridesView() {
    }

    public PerDiemOverridesView(PerDiemOverrides overrides) {
        isMileageOverridden = overrides.isMileageOverridden();
        isMealsOverridden = overrides.isMealsOverridden();
        isLodgingOverridden = overrides.isLodgingOverridden();

        mileageOverride = Double.valueOf(overrides.mileageOverride().toString());
        mealsOverride = Double.valueOf(overrides.mealsOverride().toString());
        lodgingOverride = Double.valueOf(overrides.lodgingOverride().toString());
    }

    public boolean isMileageOverridden() {
        return isMileageOverridden;
    }

    public boolean isMealsOverridden() {
        return isMealsOverridden;
    }

    public boolean isLodgingOverridden() {
        return isLodgingOverridden;
    }

    public double getMileageOverride() {
        return mileageOverride;
    }

    public double getMealsOverride() {
        return mealsOverride;
    }

    public double getLodgingOverride() {
        return lodgingOverride;
    }

    @Override
    public String getViewType() {
        return "perdiem-overrides";
    }
}
