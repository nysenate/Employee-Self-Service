package gov.nysenate.ess.travel.accommodation;

import gov.nysenate.ess.travel.utils.Dollars;

import java.time.LocalDate;
import java.util.Objects;

public class NightStay extends Stay {

    private final Dollars lodgingRate;

    public NightStay(LocalDate date, Dollars lodgingRate) {
        super(date);
        this.lodgingRate = lodgingRate;
    }

    @Override
    public Dollars lodgingAllowance() {
        return getLodgingRate();
    }

    protected Dollars getLodgingRate() {
        return lodgingRate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        NightStay nightStay = (NightStay) o;
        return Objects.equals(lodgingRate, nightStay.lodgingRate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), lodgingRate);
    }
}
