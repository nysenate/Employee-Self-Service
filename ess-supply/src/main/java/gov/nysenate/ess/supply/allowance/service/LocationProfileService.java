package gov.nysenate.ess.supply.allowance.service;

import gov.nysenate.ess.core.model.unit.LocationId;
import gov.nysenate.ess.supply.allowance.LocationProfile;

public interface LocationProfileService {

    LocationProfile getLocationProfile(LocationId locationId);
}
