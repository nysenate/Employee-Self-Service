package gov.nysenate.ess.travel.application.address;

import gov.nysenate.ess.core.model.unit.Address;

import java.util.Objects;
import java.util.UUID;

public class TravelAddress extends Address {

    private final UUID id;

    public TravelAddress(UUID id) {
        super();
        this.id = id;
    }

    public TravelAddress(UUID id, Address address) {
        super(address.getAddr1(), address.getAddr2(), address.getCity(), address.getState(), address.getZip5(), address.getZip4());
        this.id = id;
        super.setCounty(address.getCounty());
        super.setCountry(address.getCountry());
    }

    public TravelAddress(UUID id, String addr1, String addr2, String city, String state, String zip5, String zip4, String county, String country) {
        super(addr1, addr2, city, state, zip5, zip4);
        this.id = id;
        super.setCounty(county);
        super.setCountry(country);
    }

    public UUID getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        TravelAddress that = (TravelAddress) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id);
    }
}
