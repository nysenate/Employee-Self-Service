package gov.nysenate.ess.travel.application.uncompleted;

import gov.nysenate.ess.core.client.view.AddressView;
import gov.nysenate.ess.core.client.view.base.ViewObject;

import java.util.List;

public class DestinationDtoView implements ViewObject {

    AddressView address;
    List<StayDtoView> stays;

    public DestinationDtoView() {
    }

    public AddressView getAddress() {
        return address;
    }

    public List<StayDtoView> getStays() {
        return stays;
    }

    @Override
    public String getViewType() {
        return "destination-dto";
    }
}
