package gov.nysenate.ess.travel.application.allowances.meal;

import gov.nysenate.ess.travel.application.address.TravelAddress;
import gov.nysenate.ess.travel.utils.Dollars;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

public class MealAllowance {

    private final UUID id;
    private final TravelAddress address;
    private final LocalDate date;
    private final Dollars mealRate;
    private final boolean isMealsRequested;

    public MealAllowance(UUID id, TravelAddress address, LocalDate date, Dollars mealRate, boolean isMealsRequested) {
        this.id = id;
        this.address = address;
        this.date = date;
        this.mealRate = mealRate;
        this.isMealsRequested = isMealsRequested;
    }

    public Dollars allowance() {
        if (isMealsRequested()) {
            return mealRate;
        }
        return Dollars.ZERO;
    }

    protected UUID getId() {
        return id;
    }

    protected TravelAddress getAddress() {
        return address;
    }

    protected LocalDate getDate() {
        return date;
    }

    protected Dollars getMealRate() {
        return mealRate;
    }

    protected boolean isMealsRequested() {
        return isMealsRequested;
    }

    @Override
    public String toString() {
        return "MealAllowance{" +
                "id=" + id +
                ", address=" + address +
                ", date=" + date +
                ", mealRate=" + mealRate +
                ", isMealsRequested=" + isMealsRequested +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MealAllowance that = (MealAllowance) o;
        return isMealsRequested == that.isMealsRequested &&
                Objects.equals(id, that.id) &&
                Objects.equals(address, that.address) &&
                Objects.equals(date, that.date) &&
                Objects.equals(mealRate, that.mealRate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, address, date, mealRate, isMealsRequested);
    }
}
