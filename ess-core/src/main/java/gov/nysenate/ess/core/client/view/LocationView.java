package gov.nysenate.ess.core.client.view;

import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.core.model.unit.Location;
import gov.nysenate.ess.core.model.unit.LocationType;

public class LocationView implements ViewObject {

    protected String code;
    protected String locationType;
    protected char locationTypeCode;
    protected AddressView address;

    public Location toLocation() {
        Location location = new Location();
        location.setCode(code);
        location.setType(LocationType.valueOfCode(locationTypeCode));
        location.setAddress(address.toAddress());
        return location;
    }

    public LocationView() {

    }

    public LocationView(Location loc) {
        this.code = loc.getCode();
        this.locationType = loc.getType().getName();
        this.locationTypeCode = loc.getType().getCode();
        this.address = new AddressView(loc.getAddress());
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getLocationType() {
        return locationType;
    }

    public void setLocationType(String locationType) {
        this.locationType = locationType;
    }

    public char getLocationTypeCode() {
        return locationTypeCode;
    }

    public void setLocationTypeCode(char locationTypeCode) {
        this.locationTypeCode = locationTypeCode;
    }

    public AddressView getAddress() {
        return address;
    }

    public void setAddress(AddressView address) {
        this.address = address;
    }

    @Override
    public String getViewType() {
        return "location";
    }
}
