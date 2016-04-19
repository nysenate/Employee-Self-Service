package gov.nysenate.ess.core.dao.unit;

import com.google.common.collect.ImmutableCollection;
import gov.nysenate.ess.core.model.unit.Location;
import gov.nysenate.ess.core.model.unit.LocationId;
import gov.nysenate.ess.core.model.unit.LocationType;

import java.util.List;

public interface LocationDao {

    List<Location> getLocations();

    Location getLocationById(LocationId locId);

    /**
     * Search locations by code.
     * @return all locations with codes that contain the given <code>term</code>.
     */
    ImmutableCollection<Location> searchLocations(String term);
}
