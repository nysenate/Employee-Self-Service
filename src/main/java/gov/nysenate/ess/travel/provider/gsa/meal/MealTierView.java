package gov.nysenate.ess.travel.provider.gsa.meal;

import gov.nysenate.ess.core.client.view.base.ViewObject;

import java.util.UUID;

public class MealTierView implements ViewObject {

    String id;
    String tier;
    String breakfast;
    String lunch;
    String dinner;
    String incidental;

    public MealTierView() {
    }

    public MealTierView(MealTier tier) {
        this.id = tier.getId().toString();
        this.tier = tier.getTier();
        this.breakfast = tier.getBreakfast().toString();
        this.lunch = tier.getLunch().toString();
        this.dinner = tier.getDinner().toString();
        this.incidental = tier.getIncidental().toString();
    }

    public MealTier toMealTier() {
        return new MealTier(UUID.fromString(id), tier, breakfast, lunch, dinner, incidental);
    }

    public String getId() {
        return id;
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
