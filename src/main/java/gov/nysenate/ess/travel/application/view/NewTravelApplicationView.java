package gov.nysenate.ess.travel.application.view;

import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.travel.application.model.TravelApplication;

/**
 * Represents a newly submitted travel application
 */
public class NewTravelApplicationView implements ViewObject {

    private int travelerEmpId;
    private TravelAllowancesView allowances;
    private ItineraryView itinerary;
    private String purposeOfTravel;

    public NewTravelApplicationView() {
    }

    public TravelApplication.Builder toTravelApplicationBuilder() {
        return TravelApplication.Builder()
                .setAllowances(allowances.toTravelAllowances())
                .setItinerary(itinerary.toItinerary())
                .setPurposeOfTravel(purposeOfTravel);
    }


    public int getTravelerEmpId() {
        return travelerEmpId;
    }

    public TravelAllowancesView getAllowances() {
        return allowances;
    }

    public ItineraryView getItinerary() {
        return itinerary;
    }

    public String getPurposeOfTravel() {
        return purposeOfTravel;
    }

    @Override
    public String getViewType() {
        return "new-travel-application-view";
    }
}
