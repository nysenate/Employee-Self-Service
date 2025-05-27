package gov.nysenate.ess.travel.request.allowances.meal;

import gov.nysenate.ess.travel.request.address.TravelAddress;
import gov.nysenate.ess.travel.provider.senate.SenateMie;
import gov.nysenate.ess.travel.utils.Dollars;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.util.Objects;

public final class MealPerDiem {

    private int id;
    private final TravelAddress address;
    private final LocalDate date;
    /**
     * The daily meal reimbursement rate for this address and date.
     * The same as the mie.total() if mie is not null. {@code rate} should never be null,
     * however a mie may be null if the senate has not yet defined meal rates for the {@code date} yet.
     */
    private final Dollars rate;
    private final SenateMie mie;
    private boolean isBreakfastRequested;
    private boolean isDinnerRequested;
    private boolean qualifiesForBreakfast;
    private boolean qualifiesForDinner;

    public MealPerDiem(@NotNull TravelAddress address, @NotNull LocalDate date,
                       @NotNull Dollars rate, @NotNull SenateMie mie) {
        this(0, address, date, rate, mie, true, true, true, true);
    }

    public MealPerDiem(int id, @NotNull TravelAddress address, @NotNull LocalDate date, @NotNull Dollars rate,
                       @NotNull SenateMie mie, boolean isBreakfastRequested, boolean isDinnerRequested,
                       boolean qualifiesForBreakfast, boolean qualifiesForDinner) {
        this.id = id;
        this.address = Objects.requireNonNull(address);
        this.date = Objects.requireNonNull(date);
        this.rate = Objects.requireNonNull(rate);
        this.mie = Objects.requireNonNull(mie);
        this.isBreakfastRequested = isBreakfastRequested;
        this.isDinnerRequested = isDinnerRequested;
        this.qualifiesForBreakfast = qualifiesForBreakfast;
        this.qualifiesForDinner = qualifiesForDinner;
    }

    /**
     * The total reimbursement for this meal per diem.
     */
    public Dollars total() {
        return breakfast().add(dinner());
    }

    /**
     * The reimbursement for breakfast.
     */
    public Dollars breakfast() {
        if (!isBreakfastRequested() || !qualifiesForBreakfast()) {
            return Dollars.ZERO;
        }
        return mie().breakfast();
    }

    /**
     * The reimbursement for dinner.
     */
    public Dollars dinner() {
        if (!isDinnerRequested() || !qualifiesForDinner()) {
            return Dollars.ZERO;
        }
        return mie().dinner();
    }

    int id() {
        return id;
    }

    void setId(int id) {
        this.id = id;
    }

    public SenateMie mie() {
        return mie;
    }

    public TravelAddress address() {
        return address;
    }

    public LocalDate date() {
        return date;
    }

    public Dollars rate() {
        return this.rate;
    }

    public boolean isBreakfastRequested() {
        return isBreakfastRequested;
    }

    public boolean isDinnerRequested() {
        return isDinnerRequested;
    }

    public boolean qualifiesForBreakfast() {
        return qualifiesForBreakfast;
    }

    public boolean qualifiesForDinner() {
        return qualifiesForDinner;
    }

    public void setQualifiesForBreakfast(boolean qualifiesForBreakfast) {
        this.qualifiesForBreakfast = qualifiesForBreakfast;
    }

    public void setQualifiesForDinner(boolean qualifiesForDinner) {
        this.qualifiesForDinner = qualifiesForDinner;
    }

    @Override
    public String toString() {
        return "MealPerDiem{" +
                "id=" + id +
                ", address=" + address +
                ", date=" + date +
                ", rate=" + rate +
                ", mie=" + mie +
                ", isBreakfastRequested=" + isBreakfastRequested +
                ", isDinnerRequested=" + isDinnerRequested +
                ", qualifiesForBreakfast=" + qualifiesForBreakfast +
                ", qualifiesForDinner=" + qualifiesForDinner +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MealPerDiem that = (MealPerDiem) o;
        return id == that.id
                && isBreakfastRequested == that.isBreakfastRequested
                && isDinnerRequested == that.isDinnerRequested
                && qualifiesForBreakfast == that.qualifiesForBreakfast
                && qualifiesForDinner == that.qualifiesForDinner
                && Objects.equals(address, that.address)
                && Objects.equals(date, that.date)
                && Objects.equals(rate, that.rate)
                && Objects.equals(mie, that.mie);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, address, date, rate, mie, isBreakfastRequested, isDinnerRequested,
                qualifiesForBreakfast, qualifiesForDinner);
    }
}
