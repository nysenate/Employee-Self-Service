package gov.nysenate.ess.travel.application.address;

import gov.nysenate.ess.core.client.view.AddressView;

public class TravelAddressView extends AddressView {

    String id;

    public TravelAddressView() {}

    public TravelAddressView(TravelAddress travelAddress) {
        super(travelAddress);
        this.id = travelAddress.getId().toString();
    }

    public TravelAddress toTravelAddress() {
        return TravelAddressFactory.createTravelAddress(super.toAddress());
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
