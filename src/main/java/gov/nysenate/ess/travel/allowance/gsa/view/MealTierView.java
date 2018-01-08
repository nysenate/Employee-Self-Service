package gov.nysenate.ess.travel.allowance.gsa.view;

import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.travel.allowance.gsa.model.MealTier;

public class MealTierView implements ViewObject {

    private String tier;
    private String breakfast;
    private String lunch;
    private String dinner;
    private String incidental;

    public MealTierView() {
    }

    public MealTierView(MealTier mealTier) {
        this.tier = mealTier.getTier();
        this.breakfast = mealTier.getBreakfast().toString();
        this.lunch = mealTier.getLunch().toString();
        this.dinner = mealTier.getDinner().toString();
        this.incidental = mealTier.getIncidental().toString();
    }

    public MealTier toMealTier() {
        return new MealTier(getTier(), getBreakfast(), getLunch(), getDinner(), getIncidental());
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
        return "meal-rate";
    }
}
