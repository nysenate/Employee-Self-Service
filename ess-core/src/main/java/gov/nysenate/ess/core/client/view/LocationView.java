package gov.nysenate.ess.core.client.view;

import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.core.model.unit.Location;
import gov.nysenate.ess.core.model.unit.LocationId;
import gov.nysenate.ess.core.model.unit.LocationType;

public class LocationView implements ViewObject {

    protected String code;
    protected String locationType;
    protected char locationTypeCode;
    protected AddressView address;
    protected RespCenterHeadView respCenterHead;

    public LocationView() {}

    public LocationView(Location loc) {
        this.code = loc.getLocId().getCode();
        this.locationType = loc.getLocId().getType().getName();
        this.locationTypeCode = loc.getLocId().getType().getCode();
        this.address = new AddressView(loc.getAddress());
        this.respCenterHead = new RespCenterHeadView(loc.getResponsibilityHead());
    }

    public Location toLocation() {
        LocationId locId = new LocationId(this.code, this.locationTypeCode);
        return new Location(locId, address.toAddress(), respCenterHead.toResponsibilityHead());
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

    public RespCenterHeadView getRespCenterHead() {
        return respCenterHead;
    }

    @Override
    public String getViewType() {
        return "location";
    }
}
