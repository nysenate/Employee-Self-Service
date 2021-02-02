package gov.nysenate.ess.travel.provider.miles;

import com.google.maps.errors.ApiException;
import gov.nysenate.ess.travel.application.address.TravelAddress;

import java.io.IOException;

public interface MapService {

    double drivingDistance(TravelAddress from, TravelAddress to) throws InterruptedException, ApiException, IOException;
}
