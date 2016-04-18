package gov.nysenate.ess.core.dao.unit;

import gov.nysenate.ess.core.model.unit.Location;
import gov.nysenate.ess.core.model.unit.LocationId;
import gov.nysenate.ess.core.model.unit.LocationType;

import java.util.List;

public interface LocationDao {

    List<Location> getLocations();

    Location getLocationById(LocationId locId);
}
