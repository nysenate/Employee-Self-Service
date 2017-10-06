package gov.nysenate.ess.travel.travelallowance;

import gov.nysenate.ess.travel.application.dao.InMemoryTravelApplicationDao;
import gov.nysenate.ess.travel.application.model.*;
import gov.nysenate.ess.travel.maps.MapsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TravelAllowanceService {
    @Autowired
    MapsService mapsService;

    public TravelAllowanceService() {}

    public TransportationAllowance updateTravelAllowance(Itinerary itinerary) {
        //for each travelDestination, get the address and mode of transportation
        String[] destinations = new String[itinerary.getTravelDestinations().size()];
        for (TravelDestination td : itinerary.getTravelDestinations()) {
            td.getAddress().toString();
        }
             //String mileage = mapsService.getTripDistance(itinerary.getOrigin().toString(), destinations);
        return new TransportationAllowance("0", "0");
    }

    //get IRS rate, * with mileage to get total travelAllowance for TravelAllowance object
    //get tolls
    //handle mode of transportation (only PERSONAL_AUTO)
    //handle length of trip (if > 35, reimbursed for all mileage)
}
