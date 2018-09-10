package gov.nysenate.ess.travel.provider.gsa.meal;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableMap;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Contains all tiers of meal reimbursement.
 */
public class MealRates {

    private final UUID id;
    private final LocalDate startDate;
    private LocalDate endDate;
    private final ImmutableMap<String, MealTier> tiers;

    public MealRates(UUID id, LocalDate startDate, LocalDate endDate, Set<MealTier> tiers) {
        this.id = id;
        this.startDate = startDate;
        this.endDate = endDate;
        this.tiers = ImmutableMap.copyOf(tiers.stream()
                .collect(Collectors.toMap(MealTier::getTier, Function.identity())));
    }

    public MealTier getTier(String mealTier) {
        return tiers.get(mealTier);
    }

    public ImmutableCollection<MealTier> getTiers() {
        return tiers.values();
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    protected UUID getId() {
        return id;
    }

    protected LocalDate getStartDate() {
        return startDate;
    }

    protected LocalDate getEndDate() {
        return endDate;
    }

    @Override
    public String toString() {
        return "MealRates{" +
                "id=" + id +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", tiers=" + tiers +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MealRates mealRates = (MealRates) o;
        return Objects.equals(startDate, mealRates.startDate) &&
                Objects.equals(endDate, mealRates.endDate) &&
                Objects.equals(tiers, mealRates.tiers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(startDate, endDate, tiers);
    }
}
