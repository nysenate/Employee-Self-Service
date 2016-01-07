package gov.nysenate.ess.core.client.view;

import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.core.model.unit.Location;
import gov.nysenate.ess.core.model.unit.LocationType;

public class LocationView implements ViewObject {

    protected String code;
    protected String locationType;
    protected char locationTypeCode;
    protected AddressView address;

    public LocationView() {}

    public LocationView(Location loc) {
        this.code = loc.getCode();
        this.locationType = loc.getType().getName();
        this.locationTypeCode = loc.getType().getCode();
        this.address = new AddressView(loc.getAddress());
    }

    public Location toLocation() {
        Location location = new Location();
        location.setCode(code);
        location.setType(LocationType.valueOfCode(locationTypeCode));
        location.setAddress(address.toAddress());
        return location;
    }

    public String getCode() {
        return code;
    }

    public String getLocationType() {
        return locationType;
    }

    public char getLocationTypeCode() {
        return locationTypeCode;
    }

    public AddressView getAddress() {
        return address;
    }

    @Override
    public String getViewType() {
        return "location";
    }
}
