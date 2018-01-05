package gov.nysenate.ess.travel.allowance.gsa.model;

import gov.nysenate.ess.core.model.unit.Address;
import gov.nysenate.ess.travel.utils.TravelAllowanceUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

public class LodgingNight {

    private final LocalDate date;
    private final Address address;
    private final BigDecimal rate;

    public LodgingNight(LocalDate date, Address address, BigDecimal rate) {
        this.date = date;
        this.address = address;
        this.rate = TravelAllowanceUtils.round(rate);
    }

    public LocalDate getDate() {
        return date;
    }

    public Address getAddress() {
        return address;
    }

    public BigDecimal getRate() {
        return rate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LodgingNight that = (LodgingNight) o;
        return Objects.equals(date, that.date) &&
                Objects.equals(address, that.address) &&
                Objects.equals(rate, that.rate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, address, rate);
    }
}
