package gov.nysenate.ess.travel.gsa;

import com.google.gson.JsonObject;
import gov.nysenate.ess.core.model.unit.Address;
import gov.nysenate.ess.travel.application.model.GsaAllowance;
import gov.nysenate.ess.travel.application.model.Itinerary;
import gov.nysenate.ess.travel.application.model.TravelDestination;
import gov.nysenate.ess.travel.request.model.GSAClient;

import java.time.LocalDateTime;
import java.util.List;

public class GsaAllowanceService {

    public GsaAllowance computeAllowance(Itinerary itinerary) {
        Address address = itinerary.getOrigin();
        List<TravelDestination> travelDestinations = itinerary.getTravelDestinations();
        for (TravelDestination travelDestination : travelDestinations) {
            LocalDateTime arrival = travelDestination.getArrivalDateTime();
            LocalDateTime departure = travelDestination.getDepartureDateTime();
            Address destinationAddress = travelDestination.getAddress();
        }

        GSAClient client = new GSAClient(2017, 12110);
        JsonObject object = client.getRecords();

        GsaAllowance gsaAllowance = new GsaAllowance(object.get("Meals").getAsString(), "", "");


        return gsaAllowance;
    }
}
