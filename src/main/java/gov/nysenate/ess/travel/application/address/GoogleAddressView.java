package gov.nysenate.ess.travel.application.address;

import gov.nysenate.ess.core.client.view.AddressView;
import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.core.model.unit.Address;

public class GoogleAddressView extends AddressView implements ViewObject {

    private String placeId;
    private String name;
    private String formattedAddress;

    public GoogleAddressView() {
    }

    public GoogleAddressView(GoogleAddress addr) {
        super(addr);
        this.placeId = addr.getPlaceId();
        this.name = addr.getName();
        this.formattedAddress = addr.getName();
    }

    public GoogleAddress toGoogleAddress() {
        Address address = super.toAddress();
        GoogleAddress googleAddress = new GoogleAddress(placeId, name, formattedAddress);
        googleAddress.setAddr1(address.getAddr1());
        googleAddress.setAddr2(address.getAddr2());
        googleAddress.setCity(address.getCity());
        googleAddress.setCounty(address.getCounty());
        googleAddress.setCountry(address.getCountry());
        googleAddress.setState(address.getState());
        googleAddress.setZip5(address.getZip5());
        googleAddress.setZip4(address.getZip4());
        return googleAddress;
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
    public String getViewType() {
        return "google-address";
    }
}
