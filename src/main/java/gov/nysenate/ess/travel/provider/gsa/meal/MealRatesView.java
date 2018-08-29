package gov.nysenate.ess.travel.provider.gsa.meal;

import com.google.common.collect.ImmutableMap;
import gov.nysenate.ess.core.client.view.base.ViewObject;

import java.util.function.Function;
import java.util.stream.Collectors;

public class MealRatesView implements ViewObject {

    private final ImmutableMap<String, MealTierView> tiers;

    public MealRatesView(MealRates mealRates) {

        this.tiers = ImmutableMap.copyOf(mealRates.getTiers().stream()
                .map(mealTier -> new MealTierView(mealTier))
                .collect(Collectors.toMap(MealTierView::getTier, Function.identity())));
    }

    public ImmutableMap<String, MealTierView> getTiers() {
        return tiers;
    }

    public MealTierView getTier(String mealTier) {
        return tiers.get(mealTier);
    }

    @Override
    public String getViewType() {
        return "Meal-Rates-View";
    }

}
