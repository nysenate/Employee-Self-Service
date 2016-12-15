package gov.nysenate.ess.core.service.unit;

import gov.nysenate.ess.core.model.unit.Location;
import gov.nysenate.ess.core.model.unit.LocationId;
import gov.nysenate.ess.core.model.unit.LocationType;

import java.util.List;

/**
 * Service layer to retrieve Locations
 */
public interface LocationService {

    /**
     * @param locId A location id.
     * @return A {@link Location} object, or <code>null</code> if no location is found.
     */
    Location getLocation(LocationId locId);

    List<Location> getLocations();
}
