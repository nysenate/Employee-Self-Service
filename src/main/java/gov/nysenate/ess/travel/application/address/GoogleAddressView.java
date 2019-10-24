package gov.nysenate.ess.travel.application.address;

import com.fasterxml.jackson.annotation.JsonIgnore;
import gov.nysenate.ess.core.client.view.AddressView;
import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.core.model.unit.Address;

public class GoogleAddressView extends AddressView implements ViewObject {

    private int id;
    private String placeId;
    private String name;
    private String formattedAddress;
    private String formattedAddressWithCounty;

    public GoogleAddressView() {
    }

    public GoogleAddressView(GoogleAddress addr) {
        super(addr);
        this.id = addr.getId();
        this.placeId = addr.getPlaceId();
        this.name = addr.getName();
        this.formattedAddress = addr.getName();
        this.formattedAddressWithCounty = addr.getFromattedAddressWithCounty();
    }

    public GoogleAddress toGoogleAddress() {
        Address address = super.toAddress();
        GoogleAddress googleAddress = new GoogleAddress(id, placeId, name, formattedAddress);
        googleAddress.setAddr1(trimIfNotNull(address.getAddr1()));
        googleAddress.setAddr2(trimIfNotNull(address.getAddr2().trim()));
        googleAddress.setCity(trimIfNotNull(address.getCity().trim()));
        googleAddress.setCounty(trimIfNotNull(address.getCounty().trim()));
        googleAddress.setCountry(trimIfNotNull(address.getCountry().trim()));
        googleAddress.setState(trimIfNotNull(address.getState().trim()));
        googleAddress.setZip5(trimIfNotNull(address.getZip5().trim()));
        googleAddress.setZip4(trimIfNotNull(address.getZip4().trim()));
        return googleAddress;
    }

    @JsonIgnore
    private String trimIfNotNull(String s) {
        return s == null ? "" : s.trim();
    }

    public int getId() {
        return id;
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

    public String getFormattedAddressWithCounty() {
        return formattedAddressWithCounty;
    }

    @Override
    public String getViewType() {
        return "google-address";
    }
}
