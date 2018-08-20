package gov.nysenate.ess.travel.application.address;

import gov.nysenate.ess.core.client.view.AddressView;

import java.util.UUID;

public class TravelAddressView extends AddressView {

    String id;

    public TravelAddressView() {}

    public TravelAddressView(TravelAddress travelAddress) {
        super(travelAddress);
        this.id = travelAddress.getId().toString();
    }

    public TravelAddress toTravelAddress() {
        if (id == null) {
            id = UUID.randomUUID().toString();
        }
        TravelAddress address = new TravelAddress(UUID.fromString(id), addr1, addr2, city, state, zip5, zip4);
        address.setCounty(county);
        return address;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getViewType() {
        return "travel-address";
    }
}
