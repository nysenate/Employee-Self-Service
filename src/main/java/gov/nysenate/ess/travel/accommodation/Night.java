package gov.nysenate.ess.travel.accommodation;

import gov.nysenate.ess.travel.utils.Dollars;

import java.time.LocalDate;
import java.util.Objects;

/**
 * An Accommodation contains a NightStay for each night at an address.
 * The NightStay contains all info related to lodging allowances for that address and date.
 */
public class Night implements Comparable<Night> {

    private final LocalDate date;
    private final Dollars lodgingRate;
    private boolean isLodgingRequested;

    public Night(LocalDate date, Dollars lodgingRate, boolean isLodgingRequested) {
        this.date = date;
        this.lodgingRate = lodgingRate;
        this.isLodgingRequested = isLodgingRequested;
    }

    public Dollars lodgingAllowance() {
        if (isLodgingRequested()) {
            return getLodgingRate();
        }
        else {
            return Dollars.ZERO;
        }
    }

    public LocalDate getDate() {
        return date;
    }

    protected Dollars getLodgingRate() {
        return lodgingRate;
    }

    protected boolean isLodgingRequested() {
        return isLodgingRequested;
    }

    protected void setLodgingRequested(boolean isLodgingRequested) {
        this.isLodgingRequested = isLodgingRequested;
    }

    @Override
    public String toString() {
        return "Night{" +
                "date=" + date +
                ", lodgingRate=" + lodgingRate +
                ", isLodgingRequested=" + isLodgingRequested +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Night night = (Night) o;
        return isLodgingRequested == night.isLodgingRequested &&
                Objects.equals(date, night.date) &&
                Objects.equals(lodgingRate, night.lodgingRate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, lodgingRate, isLodgingRequested);
    }

    @Override
    public int compareTo(Night o) {
        int cmp = date.compareTo(o.date);
        if (cmp == 0) {
            cmp = lodgingRate.compareTo(o.lodgingRate);
            if (cmp == 0) {
                cmp = (isLodgingRequested == o.isLodgingRequested ? 0 : (isLodgingRequested ? 1 : -1));
            }
        }
        return cmp;
    }
}
