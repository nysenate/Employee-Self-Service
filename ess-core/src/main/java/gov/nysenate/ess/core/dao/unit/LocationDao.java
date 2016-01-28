package gov.nysenate.ess.core.dao.unit;

import gov.nysenate.ess.core.model.unit.Location;
import gov.nysenate.ess.core.model.unit.LocationType;

import java.util.List;

public interface LocationDao {

    /**
     * Get all active Locations.
     */
    List<Location> getLocations();

    /**
     * Get a Location by its location code and location type.
     */
    Location getLocationByCodeAndType(String locCode, LocationType type);
}
