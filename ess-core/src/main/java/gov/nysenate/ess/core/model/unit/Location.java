package gov.nysenate.ess.core.model.unit;

import gov.nysenate.ess.core.model.personnel.ResponsibilityHead;

/**
 * Typically used to represent a Senate employee's place of work or some other location
 * that serves a business purpose.
 */
public class Location
{
    protected String code;
    protected LocationType type;
    protected Address address;
    protected ResponsibilityHead responsibilityHead;

    public Location() {}

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public LocationType getType() {
        return type;
    }

    public void setType(LocationType type) {
        this.type = type;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public ResponsibilityHead getResponsibilityHead() {
        return responsibilityHead;
    }

    public void setResponsibilityHead(ResponsibilityHead responsibilityHead) {
        this.responsibilityHead = responsibilityHead;
    }

    @Override
    public String toString() {
        return "Location{" +
               "code='" + code + '\'' +
               ", type=" + type +
               ", address=" + address +
               ", responsibilityHead=" + responsibilityHead +
               '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Location location = (Location) o;

        if (code != null ? !code.equals(location.code) : location.code != null) return false;
        if (type != location.type) return false;
        if (address != null ? !address.equals(location.address) : location.address != null) return false;
        return !(responsibilityHead != null ? !responsibilityHead.equals(location.responsibilityHead) : location.responsibilityHead != null);

    }

    @Override
    public int hashCode() {
        int result = code != null ? code.hashCode() : 0;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (address != null ? address.hashCode() : 0);
        result = 31 * result + (responsibilityHead != null ? responsibilityHead.hashCode() : 0);
        return result;
    }
}
