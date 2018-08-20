package gov.nysenate.ess.travel.fixtures;

import gov.nysenate.ess.travel.provider.gsa.meal.MealRates;
import gov.nysenate.ess.travel.provider.gsa.meal.MealTier;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class MealRatesFixture {

    public static MealRates mealRatesFor2018() {
        Set<MealTier> tiers = new HashSet<>();
        tiers.add(new MealTier(UUID.randomUUID(), "51", "11", "12", "23", "5"));
        tiers.add(new MealTier(UUID.randomUUID(), "54", "12", "13", "24", "5"));
        tiers.add(new MealTier(UUID.randomUUID(), "59", "13", "15", "26", "5"));
        tiers.add(new MealTier(UUID.randomUUID(), "64", "15", "16", "28", "5"));
        tiers.add(new MealTier(UUID.randomUUID(), "69", "16", "17", "31", "5"));
        tiers.add(new MealTier(UUID.randomUUID(), "74", "17", "18", "34", "5"));
        return new MealRates(UUID.randomUUID(), LocalDate.now(), LocalDate.now(), tiers);
    }
}
