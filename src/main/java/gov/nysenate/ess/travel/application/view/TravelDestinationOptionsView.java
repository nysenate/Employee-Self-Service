package gov.nysenate.ess.travel.application.view;

import gov.nysenate.ess.travel.application.model.ModeOfTransportation;
import gov.nysenate.ess.travel.application.model.TravelDestinationOptions;

public class TravelDestinationOptionsView {

    private String modeOfTransportation;
    private boolean requestMeals;
    private boolean requestLodging;
    private boolean requestMileage;

    public TravelDestinationOptionsView(TravelDestinationOptions options) {
        this.modeOfTransportation = options.getModeOfTransportation().getDisplayName();
        this.requestMeals = options.isRequestMeals();
        this.requestLodging = options.isRequestLodging();
        this.requestMileage = options.isRequestMileage();
    }

    public TravelDestinationOptions toTravelDestinationOptions() {
        return new TravelDestinationOptions(ModeOfTransportation.of(
                modeOfTransportation), requestMeals, requestLodging, requestMileage);
    }

    public String getModeOfTransportation() {
        return modeOfTransportation;
    }

    public boolean isRequestMeals() {
        return requestMeals;
    }

    public boolean isRequestLodging() {
        return requestLodging;
    }

    public boolean isRequestMileage() {
        return requestMileage;
    }
}
