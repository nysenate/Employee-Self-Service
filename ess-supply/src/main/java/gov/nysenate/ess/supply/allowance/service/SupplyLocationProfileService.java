package gov.nysenate.ess.supply.allowance.service;

import gov.nysenate.ess.core.model.unit.Location;
import gov.nysenate.ess.core.model.unit.LocationId;
import gov.nysenate.ess.core.service.unit.LocationService;
import gov.nysenate.ess.supply.allowance.LocationProfile;
import gov.nysenate.ess.supply.allowance.dao.ItemAllowanceDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SupplyLocationProfileService implements LocationProfileService {

    @Autowired private ItemAllowanceDao itemAllowanceDao;
    @Autowired private LocationService locationService;

    @Override
    public LocationProfile getLocationProfile(LocationId locationId) {
        Location location = locationService.getLocation(locationId);
        return new LocationProfile(location, itemAllowanceDao.getItemAllowances(locationId));
    }
}
