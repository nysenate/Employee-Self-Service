package gov.nysenate.ess.travel.maps;

import com.google.maps.errors.ApiException;
import gov.nysenate.ess.core.model.unit.Address;
import gov.nysenate.ess.travel.allowance.mileage.model.Leg;
import gov.nysenate.ess.travel.application.model.Itinerary;

import java.io.IOException;
import java.util.List;

public interface MapService {

    long getLegDistance(Leg leg) throws InterruptedException, ApiException, IOException;
}
