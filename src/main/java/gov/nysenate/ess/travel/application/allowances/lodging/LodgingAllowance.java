package gov.nysenate.ess.travel.application.allowances.lodging;

import gov.nysenate.ess.core.model.unit.Address;
import gov.nysenate.ess.travel.utils.Dollars;

import java.time.LocalDate;
import java.util.Objects;

public class LodgingAllowance {

    private final Address address;
    private final LocalDate date;
    private final Dollars lodgingRate;
    private final boolean isLodgingRequested;

    public LodgingAllowance(Address address, LocalDate date, Dollars lodgingRate, boolean isLodgingRequested) {
        this.address = address;
        this.date = date;
        this.lodgingRate = lodgingRate;
        this.isLodgingRequested = isLodgingRequested;
    }

    public Dollars allowance() {
        if (isLodgingRequested()) {
            return lodgingRate;
        }
        return Dollars.ZERO;
    }

    protected Address getAddress() {
        return address;
    }

    protected LocalDate getDate() {
        return date;
    }

    protected Dollars getLodgingRate() {
        return lodgingRate;
    }

    protected boolean isLodgingRequested() {
        return isLodgingRequested;
    }

    @Override
    public String toString() {
        return "LodgingAllowance{" +
                "address=" + address +
                ", date=" + date +
                ", lodgingRate=" + lodgingRate +
                ", isLodgingRequested=" + isLodgingRequested +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LodgingAllowance that = (LodgingAllowance) o;
        return isLodgingRequested == that.isLodgingRequested &&
                Objects.equals(address, that.address) &&
                Objects.equals(date, that.date) &&
                Objects.equals(lodgingRate, that.lodgingRate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(address, date, lodgingRate, isLodgingRequested);
    }
}
