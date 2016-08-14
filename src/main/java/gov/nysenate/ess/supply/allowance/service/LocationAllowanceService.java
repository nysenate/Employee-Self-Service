package gov.nysenate.ess.supply.allowance.service;

import gov.nysenate.ess.core.model.unit.LocationId;
import gov.nysenate.ess.supply.allowance.LocationAllowance;

public interface LocationAllowanceService {

    LocationAllowance getLocationAllowance(LocationId locationId);
}
