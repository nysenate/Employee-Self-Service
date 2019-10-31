package gov.nysenate.ess.travel.application.address;

import gov.nysenate.ess.core.model.unit.Address;

import java.util.Objects;

public class GoogleAddress extends Address {

    private int id;
    private String placeId;
    private String name;
    private String formattedAddress;

    public GoogleAddress(int id, String placeId, String name, String formattedAddress) {
        this.id = id;
        // If we dont have values, use empty string instead of null so that db unique constraint works.
        this.placeId = placeId == null ? "" : placeId;
        this.name = name == null ? "" : name.trim();
        this.formattedAddress = formattedAddress == null ? "" : formattedAddress.trim();
    }

    /**
     * A formatted version of this address including county info in parenthesis.
     * Usually just the address but for some types of addresses, like establishments, it will use
     * the name of the establishment instead of street1.
     *
     * Overrides a method in {@link Address} to provide a more accurate description for GooglgeAddress's.
     *
     * @return
     */
    @Override
    public String getFormattedAddressWithCounty() {
        String desc = getName().isEmpty() ? getAddr1() : getName();
        desc += getCity().isEmpty() ? "" : ", " + getCity();
        desc += getState().isEmpty() ? "" : ", " + getState();
        desc += getZip5().isEmpty() ? "" : " " + getZip5();
        desc += getCounty().isEmpty() ? "" : " (" + getCounty() + " County)";
        desc.trim();
        return desc;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
        return id == that.id &&
                Objects.equals(placeId, that.placeId) &&
                Objects.equals(name, that.name) &&
                Objects.equals(formattedAddress, that.formattedAddress);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id, placeId, name, formattedAddress);
    }
}
