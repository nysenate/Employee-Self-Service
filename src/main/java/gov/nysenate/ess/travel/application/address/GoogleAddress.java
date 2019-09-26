package gov.nysenate.ess.travel.application.address;

import gov.nysenate.ess.core.model.unit.Address;

import java.util.Objects;

public class GoogleAddress extends Address {

    private String placeId;
    private String name;
    private String formattedAddress;

    // TODO Temporary until database is updated to use GoogleAddress.
    public GoogleAddress() {
    }

    public GoogleAddress(String placeId, String name, String formattedAddress) {
        this.placeId = placeId;
        this.name = name;
        this.formattedAddress = formattedAddress;
    }

    public String getPlaceId() {
        return placeId;
    }

    public String getName() {
        return name;
    }

    public String getFormattedAddress() {
        return formattedAddress;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        GoogleAddress that = (GoogleAddress) o;
        return Objects.equals(placeId, that.placeId) &&
                Objects.equals(name, that.name) &&
                Objects.equals(formattedAddress, that.formattedAddress);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), placeId, name, formattedAddress);
    }
}
