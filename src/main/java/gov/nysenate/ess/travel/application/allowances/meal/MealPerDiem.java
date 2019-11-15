package gov.nysenate.ess.travel.application.allowances.meal;

import gov.nysenate.ess.travel.application.address.GoogleAddress;
import gov.nysenate.ess.travel.provider.senate.SenateMie;
import gov.nysenate.ess.travel.utils.Dollars;

import java.time.LocalDate;
import java.util.Objects;

public final class MealPerDiem {

    private int id;
    private final GoogleAddress address;
    private final LocalDate date;
    /**
     * The daily meal reimbursement rate for this address and date.
     * The same as the mie.total() if mie is not null. {@code rate} should never be null,
     * however an mie may be null if the senate has not yet defined meal rates for the {@code date} yet.
     */
    private final Dollars rate;
    private final SenateMie mie;
    private boolean isReimbursementRequested;

    public MealPerDiem(GoogleAddress address, LocalDate date, Dollars rate, SenateMie mie) {
        this(0, address, date, rate, mie, true);
    }

    public MealPerDiem(int id, GoogleAddress address, LocalDate date, Dollars rate, SenateMie mie, boolean isReimbursementRequested) {
        this.id = id;
        this.address = address;
        this.date = date;
        this.rate = rate;
        this.mie = mie;
        this.isReimbursementRequested = isReimbursementRequested;
    }

    public int id() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Dollars maximumPerDiem() {
        return this.rate;
    }

    public Dollars requestedPerDiem() {
        return isReimbursementRequested() ? maximumPerDiem() : Dollars.ZERO;
    }

    public SenateMie mie() {
        return mie;
    }

    public GoogleAddress address() {
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

    @Override
    public String toString() {
        return "MealPerDiem{" +
                "id=" + id +
                ", address=" + address +
                ", date=" + date +
                ", rate=" + rate +
                ", mie=" + mie +
                ", isReimbursementRequested=" + isReimbursementRequested +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MealPerDiem that = (MealPerDiem) o;
        return id == that.id &&
                isReimbursementRequested == that.isReimbursementRequested &&
                Objects.equals(address, that.address) &&
                Objects.equals(date, that.date) &&
                Objects.equals(rate, that.rate) &&
                Objects.equals(mie, that.mie);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, address, date, rate, mie, isReimbursementRequested);
    }
}
