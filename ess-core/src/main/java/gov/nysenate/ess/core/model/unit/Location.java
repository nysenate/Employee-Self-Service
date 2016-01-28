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
}
