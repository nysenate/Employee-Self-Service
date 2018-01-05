package gov.nysenate.ess.travel.fixtures;

import gov.nysenate.ess.travel.allowance.gsa.model.MealRates;
import gov.nysenate.ess.travel.allowance.gsa.model.MealTier;

import java.util.HashSet;
import java.util.Set;

public class MealRatesFixture {

    public static MealRates mealRatesFor2018() {
        Set<MealTier> tiers = new HashSet<>();
        tiers.add(new MealTier("51", "11", "12", "23", "5"));
        tiers.add(new MealTier("54", "12", "13", "24", "5"));
        tiers.add(new MealTier("59", "13", "15", "26", "5"));
        tiers.add(new MealTier("64", "15", "16", "28", "5"));
        tiers.add(new MealTier("69", "16", "17", "31", "5"));
        tiers.add(new MealTier("74", "17", "18", "34", "5"));
        return new MealRates(tiers);
    }
}
