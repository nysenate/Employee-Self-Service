package gov.nysenate.ess.travel.travelallowance;

import gov.nysenate.ess.travel.application.dao.IrsRateDao;
import gov.nysenate.ess.travel.application.model.*;
import gov.nysenate.ess.travel.maps.MapsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

import java.util.ArrayList;

@Service
public class TravelAllowanceService {
    @Autowired
    MapsService mapsService;

    @Autowired
    IrsRateDao irsRateDao;

    public TransportationAllowance updateTravelAllowance(Itinerary itinerary) {
        List<TravelDestination> travelDestinations = itinerary.getTravelDestinations();
        String[] destinations = new String[travelDestinations.size()];

        for (int i = 0; i < destinations.length; i++) {
            TravelDestination td = itinerary.getTravelDestinations().get(i);
            destinations[i] = td.getAddress().toString();
        }
        double milesTraveled = mapsService.getTripDistance(itinerary.getOrigin().toString(), destinations);
        double mileageAllowance = 0;
        if (mapsService.getDistanceOut(itinerary.getOrigin().toString(), destinations) > 35) {
            mileageAllowance = milesTraveled * irsRateDao.getIrsRate() / 100;
        }

        return new TransportationAllowance(mileageAllowance + "", "0");
    }
    //get tolls
}
