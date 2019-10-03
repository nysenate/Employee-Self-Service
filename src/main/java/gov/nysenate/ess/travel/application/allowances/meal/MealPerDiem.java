package gov.nysenate.ess.travel.application.allowances.meal;

import gov.nysenate.ess.core.model.unit.Address;
import gov.nysenate.ess.travel.application.allowances.PerDiem;
import gov.nysenate.ess.travel.utils.Dollars;

import java.time.LocalDate;
import java.util.Objects;

public final class MealPerDiem {

    private final Address address;
    private final PerDiem perDiem;
    private boolean isReimbursementRequested;
    private Dollars overrideRate;

    public MealPerDiem(Address address, PerDiem perDiem) {
        this.address = address;
        this.perDiem = perDiem;
        this.isReimbursementRequested = true;
        this.overrideRate = Dollars.ZERO;
    }

    public MealPerDiem(Address address, PerDiem perDiem, boolean isReimbursementRequested, Dollars overrideRate) {
        this.address = address;
        this.perDiem = perDiem;
        this.isReimbursementRequested = isReimbursementRequested;
        this.overrideRate = overrideRate;
    }

    public Dollars maximumPerDiem() {
        return rate();
    }

    public Dollars requestedPerDiem() {
        return isReimbursementRequested() ? maximumPerDiem() : Dollars.ZERO;
    }

    public Address address() {
        return address;
    }

    public LocalDate date() {
        return perDiem.getDate();
    }

    public Dollars rate() {
        return new Dollars(perDiem.getRate());
    }

    public boolean isReimbursementRequested() {
        return isReimbursementRequested;
    }

    @Override
    public String toString() {
        return "MealPerDiem{" +
                "address=" + address +
                ", perDiem=" + perDiem +
                ", isReimbursementRequested=" + isReimbursementRequested +
                ", overrideRate=" + overrideRate +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MealPerDiem that = (MealPerDiem) o;
        return isReimbursementRequested == that.isReimbursementRequested &&
                Objects.equals(address, that.address) &&
                Objects.equals(perDiem, that.perDiem) &&
                Objects.equals(overrideRate, that.overrideRate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(address, perDiem, isReimbursementRequested, overrideRate);
    }
}
