package gov.nysenate.ess.travel.allowance.mileage;

import gov.nysenate.ess.core.model.unit.Address;
import gov.nysenate.ess.travel.application.model.*;
import gov.nysenate.ess.travel.maps.MapsService;
import gov.nysenate.ess.travel.maps.TripDistance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

import java.util.ArrayList;

@Service
public class MileageAllowanceService {

    @Autowired
    private MapsService mapsService;

    @Autowired
    private IrsRateDao irsRateDao;

    public BigDecimal calculateMileageAllowance(Itinerary itinerary) {
        List<Address> travelRoute = itinerary.travelRoute();

        TripDistance tripDistance = mapsService.getTripDistance(travelRoute);
        BigDecimal mileageAllowance = new BigDecimal(0);
        if (tripDistance.getTripDistanceOut() > 35) {
            mileageAllowance = BigDecimal.valueOf(tripDistance.getTripDistanceTotal()).multiply(BigDecimal.valueOf(irsRateDao.getIrsRate() / 100));
        }

        return mileageAllowance;
    }
}
