package gov.nysenate.ess.core.service.unit;

import gov.nysenate.ess.core.model.unit.Location;
import gov.nysenate.ess.core.model.unit.LocationType;

import java.util.List;

/**
 * Service layer to retrieve Locations
 */
public interface LocationService {

    /** Get location by the string made up of location code + '-' + location type. */
    Location getLocation(String locationId);

    Location getLocation(String locCode, LocationType locType);

    List<Location> getLocations();
}
