package gov.nysenate.ess.travel.application.uncompleted;

import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.travel.application.destination.DestinationView;

import java.util.List;

public class DestinationDtoView implements ViewObject {

    DestinationView accommodation;
    List<StayDtoView> stays;

    public DestinationDtoView() {
    }

    public DestinationView getAccommodation() {
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
