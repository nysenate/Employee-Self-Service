package gov.nysenate.ess.travel.allowance.gsa.model;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableMap;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Contains all tiers of meal reimbursement.
 */
public class MealRates {

    private final ImmutableMap<String, MealTier> tiers;

    public MealRates(Set<MealTier> tiers) {
        this.tiers = ImmutableMap.copyOf(tiers.stream()
                .collect(Collectors.toMap(MealTier::getTier, Function.identity())));
    }

    public MealTier getTier(String mealTier) {
        return tiers.get(mealTier);
    }

    public ImmutableCollection<MealTier> getTiers() {
        return tiers.values();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MealRates mealRates = (MealRates) o;
        return Objects.equals(tiers, mealRates.tiers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tiers);
    }
}
