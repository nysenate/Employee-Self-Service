package gov.nysenate.ess.core.model.unit;

import gov.nysenate.ess.core.model.personnel.ResponsibilityHead;

/**
 * Typically used to represent a Senate employee's place of work or some other location
 * that serves a business purpose.
 */
public final class Location
{
    private final LocationId locId;
    private Address address;
    private ResponsibilityHead responsibilityHead;
    private String locationDescription;

    public Location(LocationId locId) {
        this.locId = locId;
    }

    public Location(LocationId locId, Address address, ResponsibilityHead responsibilityHead) {
        this.locId = locId;
        this.address = address;
        this.responsibilityHead = responsibilityHead;
    }

    public Location(LocationId locId, Address address, ResponsibilityHead responsibilityHead, String locationDescription) {
        this.locId = locId;
        this.address = address;
        this.responsibilityHead = responsibilityHead;
        this.locationDescription = locationDescription;
    }

    public LocationId getLocId() {
        return locId;
    }

    public String getLocationDescription() {
        return locationDescription;
    }

    public Address getAddress() {
        return address;
    }

    public ResponsibilityHead getResponsibilityHead() {
        return responsibilityHead;
    }

    @Override
    public String toString() {
        return this.locId.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Location location = (Location) o;
        return locId != null ? locId.equals(location.locId) : location.locId == null;

    }

    @Override
    public int hashCode() {
        return locId != null ? locId.hashCode() : 0;
    }
}
