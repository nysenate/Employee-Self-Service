package gov.nysenate.ess.travel.application.uncompleted;

import gov.nysenate.ess.core.client.view.AddressView;
import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.travel.accommodation.AccommodationView;

import java.util.List;

public class DestinationDtoView implements ViewObject {

    AccommodationView accommodation;
    List<StayDtoView> stays;

    public DestinationDtoView() {
    }

    public AccommodationView getAccommodation() {
        return accommodation;
    }

    public List<StayDtoView> getStays() {
        return stays;
    }

    @Override
    public String getViewType() {
        return "destination-dto";
    }
}
