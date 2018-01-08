package gov.nysenate.ess.travel.allowance.gsa.model;

import gov.nysenate.ess.core.model.unit.Address;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

public class MealDay {

    private final LocalDate date;
    private final Address address;
    private final MealTier tier;

    public MealDay(LocalDate date, Address address, MealTier mealTier) {
        this.date = date;
        this.address = address;
        this.tier = mealTier;
    }

    public BigDecimal getSenateRate() {
        return getTier().getBreakfast().add(getTier().getDinner());
    }

    public LocalDate getDate() {
        return date;
    }

    public Address getAddress() {
        return address;
    }

    public MealTier getTier() {
        return tier;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MealDay mealDay = (MealDay) o;
        return Objects.equals(date, mealDay.date) &&
                Objects.equals(address, mealDay.address) &&
                Objects.equals(tier, mealDay.tier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, address, tier);
    }
}
