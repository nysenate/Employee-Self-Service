package gov.nysenate.ess.travel.allowance.mileage;

import gov.nysenate.ess.travel.application.model.*;
import gov.nysenate.ess.travel.maps.MapsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

import java.util.ArrayList;

@Service
public class MileageAllowanceService {

    @Autowired
    private MapsService mapsService;

    @Autowired
    private IrsRateDao irsRateDao;

    public BigDecimal calculateMileageAllowance(Itinerary itinerary) {
        List<TravelDestination> travelDestinations = itinerary.getDestinations();
        String[] destinations = new String[travelDestinations.size()];

        for (int i = 0; i < destinations.length; i++) {
            TravelDestination td = itinerary.getDestinations().get(i);
            destinations[i] = td.getAddress().toString();
        }
        double milesTraveled = mapsService.getTripDistance(itinerary.getOrigin().toString(), destinations);
        BigDecimal mileageAllowance = new BigDecimal(0);
        if (mapsService.getDistanceOut(itinerary.getOrigin().toString(), destinations) > 35) {
            mileageAllowance = BigDecimal.valueOf(milesTraveled).multiply(BigDecimal.valueOf(irsRateDao.getIrsRate() / 100));
        }

        return mileageAllowance;
    }
}
