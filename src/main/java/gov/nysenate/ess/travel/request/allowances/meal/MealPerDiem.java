package gov.nysenate.ess.travel.request.allowances.meal;

import gov.nysenate.ess.travel.request.address.TravelAddress;
import gov.nysenate.ess.travel.provider.senate.SenateMie;
import gov.nysenate.ess.travel.utils.Dollars;

import java.time.LocalDate;
import java.util.Objects;

public final class MealPerDiem {

    private int id;
    private final TravelAddress address;
    private final LocalDate date;
    /**
     * The daily meal reimbursement rate for this address and date.
     * The same as the mie.total() if mie is not null. {@code rate} should never be null,
     * however an mie may be null if the senate has not yet defined meal rates for the {@code date} yet.
     */
    private final Dollars rate;
    private final SenateMie mie;
    private boolean isReimbursementRequested;
    private boolean qualifiesForBreakfast;
    private boolean qualifiesForDinner;

    public MealPerDiem(TravelAddress address, LocalDate date, Dollars rate, SenateMie mie) {
        this(0, address, date, rate, mie, true, true, true);
    }

    public MealPerDiem(int id, TravelAddress address, LocalDate date, Dollars rate, SenateMie mie,
                       boolean isReimbursementRequested, boolean qualifiesForBreakfast, boolean qualifiesForDinner) {
        this.id = id;
        this.address = address;
        this.date = date;
        this.rate = rate;
        this.mie = mie;
        this.isReimbursementRequested = isReimbursementRequested;
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
        if (!isReimbursementRequested()) {
            return Dollars.ZERO;
        }
        return qualifiesForBreakfast() ? mie().breakfast() : Dollars.ZERO;
    }

    /**
     * The reimbursement for dinner.
     */
    public Dollars dinner() {
        if (!isReimbursementRequested()) {
            return Dollars.ZERO;
        }
        return qualifiesForDinner() ? mie().dinner() : Dollars.ZERO;
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

    public boolean isReimbursementRequested() {
        return isReimbursementRequested;
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
                ", isReimbursementRequested=" + isReimbursementRequested +
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
                && isReimbursementRequested == that.isReimbursementRequested
                && qualifiesForBreakfast == that.qualifiesForBreakfast
                && qualifiesForDinner == that.qualifiesForDinner
                && Objects.equals(address, that.address)
                && Objects.equals(date, that.date)
                && Objects.equals(rate, that.rate)
                && Objects.equals(mie, that.mie);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, address, date, rate, mie, isReimbursementRequested,
                qualifiesForBreakfast, qualifiesForDinner);
    }
}
