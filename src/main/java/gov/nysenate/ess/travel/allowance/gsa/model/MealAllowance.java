package gov.nysenate.ess.travel.allowance.gsa.model;

import com.google.common.collect.ImmutableSet;

import java.math.BigDecimal;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public class MealAllowance {

    private final ImmutableSet<MealDay> mealDays;

    public MealAllowance() {
        mealDays = ImmutableSet.of();
    }

    public MealAllowance (Set<MealDay> mealDays) {
        this.mealDays = ImmutableSet.copyOf(mealDays);
    }

    public MealAllowance(ImmutableSet<MealDay> mealDays) {
        this.mealDays = mealDays;
    }

    public MealAllowance add(MealAllowance allowance) {
        return new MealAllowance(ImmutableSet.<MealDay>builder()
                .addAll(getMealDays())
                .addAll(allowance.getMealDays())
                .build());
    }

    public MealAllowance addMealDay(MealDay mealDay) {
        return new MealAllowance(ImmutableSet.<MealDay>builder()
                .addAll(getMealDays())
                .add(mealDay)
                .build());
    }

    public BigDecimal getTotal() {
        return getMealDays().stream()
                .map(MealDay::getSenateRate)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public ImmutableSet<MealDay> getMealDays() {
        return mealDays;
    }
}
