package gov.nysenate.ess.travel.application.allowances.meal;

import gov.nysenate.ess.core.model.unit.Address;
import gov.nysenate.ess.travel.application.allowances.PerDiem;
import gov.nysenate.ess.travel.utils.Dollars;

import java.time.LocalDate;
import java.util.Objects;

public final class MealPerDiem {

    private final Address address;
    private final PerDiem perDiem;

    public MealPerDiem(Address address, PerDiem perDiem) {
        this.address = address;
        this.perDiem = perDiem;
    }

    public Dollars totalRequestedAllowance() {
        return isReimbursementRequested() ? getDollars() : Dollars.ZERO;
    }

    public Address getAddress() {
        return address;
    }

    public LocalDate getDate() {
        return perDiem.getDate();
    }

    public Dollars getDollars() {
        return perDiem.getDollars();
    }

    public boolean isReimbursementRequested() {
        return perDiem.isReimbursementRequested();
    }

    @Override
    public String toString() {
        return "MealPerDiem{" +
                "address=" + address +
                ", perDiem=" + perDiem +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MealPerDiem that = (MealPerDiem) o;
        return Objects.equals(address, that.address) &&
                Objects.equals(perDiem, that.perDiem);
    }

    @Override
    public int hashCode() {
        return Objects.hash(address, perDiem);
    }
}
