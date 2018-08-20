package gov.nysenate.ess.travel.application.allowances.lodging;

import gov.nysenate.ess.travel.application.address.TravelAddress;
import gov.nysenate.ess.travel.utils.Dollars;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

public class LodgingAllowance {

    private final UUID id;
    private final TravelAddress address;
    private final LocalDate date;
    private final Dollars lodgingRate;
    private final boolean isLodgingRequested;

    public LodgingAllowance(UUID id, TravelAddress address, LocalDate date, Dollars lodgingRate, boolean isLodgingRequested) {
        this.id = id;
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

    protected UUID getId() {
        return id;
    }

    protected TravelAddress getAddress() {
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
