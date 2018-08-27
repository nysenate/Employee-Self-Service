package gov.nysenate.ess.travel.provider.gsa.meal;

import gov.nysenate.ess.core.client.view.base.ViewObject;

public class MealTierView implements ViewObject {

    String tier;
    String total;
    String incidental;

    public MealTierView() {
    }

    public MealTierView(MealTier tier) {
        this.tier = tier.getTier();
        this.total = tier.getTotal().toString();
        this.incidental = tier.getIncidental().toString();
    }

    public MealTier toMealTier() {
        return new MealTier(tier, total, incidental);
    }

    public String getTier() {
        return tier;
    }

    public String getTotal() {
        return total;
    }

    public String getIncidental() {
        return incidental;
    }

    @Override
    public String getViewType() {
        return "meal-tier";
    }
}
