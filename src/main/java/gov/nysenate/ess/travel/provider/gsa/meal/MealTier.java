package gov.nysenate.ess.travel.provider.gsa.meal;

import gov.nysenate.ess.travel.utils.Dollars;

import java.util.Objects;
import java.util.UUID;

/**
 * Represents a single row in the GSA Meals and Incidental Expenses breakdown
 * available at http://www.gsa.gov/mie.
 */
public class MealTier implements Comparable<MealTier> {

    private final UUID id;
    private final String tier; // Also known as M&IE Total.
    private final Dollars total;
    private final Dollars incidental;

    // TODO Remove?
    public MealTier(UUID id, String tier, String breakfast, String lunch, String dinner, String incidental) {
        this.id = id;
        this.tier = tier;
        this.total = new Dollars(breakfast).add(new Dollars(lunch)).add(new Dollars(dinner));
        this.incidental = new Dollars(incidental);
    }

    public MealTier(UUID id, String tier, String total, String incidental) {
        this.id = id;
        this.tier = tier;
        this.total = new Dollars(total);
        this.incidental = new Dollars(incidental);
    }

    /**
     * The senate only allows reimbursement for breakfast and dinner, however the amount reimbursed
     * for each of those meals is different then GSA. The senate gives more for those meals such that
     * the GSA total and senate total is the same. Therefore we use the GSA total as the total meal reimbursement.
     * @return the Senate provided meal allowance for this meal tier.
     */
    public Dollars total() {
        return total.add(getIncidental());
    }

    public UUID getId() {
        return id;
    }

    protected String getTier() {
        return tier;
    }

    protected Dollars getIncidental() {
        return incidental;
    }

    protected Dollars getTotal() {
        return total;
    }

    @Override
    public String toString() {
        return "MealTier{" +
                "id=" + id +
                ", tier='" + tier + '\'' +
                ", total=" + total +
                ", incidental=" + incidental +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MealTier mealTier = (MealTier) o;
        return Objects.equals(id, mealTier.id) &&
                Objects.equals(tier, mealTier.tier) &&
                Objects.equals(total, mealTier.total) &&
                Objects.equals(incidental, mealTier.incidental);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, tier, total, incidental);
    }

    @Override
    public int compareTo(MealTier o) {
        int cmp = tier.compareTo(o.tier);
        if (cmp == 0) {
            cmp = total.compareTo(o.total);
            if (cmp == 0) {
                cmp = incidental.compareTo(o.incidental);
            }
        }
        return cmp;
    }
}