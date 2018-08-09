package gov.nysenate.ess.travel.provider.gsa.meal;

import gov.nysenate.ess.core.client.view.base.ViewObject;

public class MealTierView implements ViewObject {

    String tier;
    String breakfast;
    String lunch;
    String dinner;
    String incidental;

    public MealTierView() {
    }

    public MealTierView(MealTier tier) {
        this.tier = tier.getTier();
        this.breakfast = tier.getBreakfast().toString();
        this.lunch = tier.getLunch().toString();
        this.dinner = tier.getDinner().toString();
        this.incidental = tier.getIncidental().toString();
    }

    public MealTier toMealTier() {
        return new MealTier(tier, breakfast, lunch, dinner, incidental);
    }

    public String getTier() {
        return tier;
    }

    public String getBreakfast() {
        return breakfast;
    }

    public String getLunch() {
        return lunch;
    }

    public String getDinner() {
        return dinner;
    }

    public String getIncidental() {
        return incidental;
    }

    @Override
    public String getViewType() {
        return "meal-tier";
    }
}
