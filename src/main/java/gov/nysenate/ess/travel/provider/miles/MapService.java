package gov.nysenate.ess.travel.provider.miles;

import com.google.maps.errors.ApiException;
import gov.nysenate.ess.travel.application.address.GoogleAddress;

import java.io.IOException;

public interface MapService {

    double drivingDistance(GoogleAddress from, GoogleAddress to) throws InterruptedException, ApiException, IOException;
}
