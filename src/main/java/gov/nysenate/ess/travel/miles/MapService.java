package gov.nysenate.ess.travel.miles;

import com.google.maps.errors.ApiException;
import gov.nysenate.ess.core.model.unit.Address;

import java.io.IOException;

public interface MapService {

    double drivingDistance(Address from, Address to) throws InterruptedException, ApiException, IOException;
}
