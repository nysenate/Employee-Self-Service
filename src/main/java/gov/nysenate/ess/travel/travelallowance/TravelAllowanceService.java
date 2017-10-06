package gov.nysenate.ess.travel.travelallowance;

import gov.nysenate.ess.travel.application.dao.InMemoryTravelApplicationDao;
import gov.nysenate.ess.travel.application.model.*;
import gov.nysenate.ess.travel.maps.MapsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class TravelAllowanceService {
    @Autowired
    MapsService mapsService;

    public TravelAllowanceService() {}

    public TransportationAllowance updateTravelAllowance(Itinerary itinerary) {
        return new TransportationAllowance("0", "0");
    }

    //get IRS rate, * with mileage to get total travelAllowance for TravelAllowance object
    //get tolls
    //handle mode of transportation (only PERSONAL_AUTO)
    //rental cars???
    //handle length of trip (if > 35, reimbursed for all mileage)
}
