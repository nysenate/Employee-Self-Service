package gov.nysenate.ess.travel.application.overrides.perdiem;

import com.fasterxml.jackson.annotation.JsonProperty;
import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.travel.utils.Dollars;

public class PerDiemOverridesView implements ViewObject {

    boolean isMileageOverridden;
    boolean isMealsOverridden;
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

    public PerDiemOverrides toPerDiemOverrides() {
        PerDiemOverrides overrides = new PerDiemOverrides();
        overrides.setMileageOverride(new Dollars(mileageOverride));
        overrides.setMealsOverride(new Dollars(mealsOverride));
        overrides.setLodgingOverride(new Dollars(lodgingOverride));
        return overrides;
    }

    @JsonProperty("isMileageOverridden")
    public boolean isMileageOverridden() {
        return isMileageOverridden;
    }

    @JsonProperty("isMealsOverridden")
    public boolean isMealsOverridden() {
        return isMealsOverridden;
    }

    @JsonProperty("isLodgingOverridden")
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
