package gov.nysenate.ess.supply.destination;

import gov.nysenate.ess.core.dao.security.authorization.RoleDao;
import gov.nysenate.ess.core.model.auth.EssRole;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.model.personnel.ResponsibilityHead;
import gov.nysenate.ess.core.model.unit.Location;
import gov.nysenate.ess.core.service.base.LocationService;
import gov.nysenate.ess.supply.authorization.responsibilityhead.TempResponsibilityHeadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Set;

@Service
public class DestinationService {
    private final LocationService locationService;
    private final TempResponsibilityHeadService trchService;
    private final RoleDao roleDao;
    private final AllowedDestinationService allowedDestinationService;

    @Autowired
    public DestinationService(LocationService locationService, TempResponsibilityHeadService trchService,
                              RoleDao roleDao, AllowedDestinationService allowedDestinationService) {
        this.locationService = locationService;
        this.trchService = trchService;
        this.roleDao = roleDao;
        this.allowedDestinationService = allowedDestinationService;
    }

    public Set<Location> employeeDestinations(Employee employee) {
        Collection<Location> possibleLocations = locationService.getLocations(true);
        Collection<ResponsibilityHead> tempRchs = trchService.tempRchForEmp(employee);
        Collection<EssRole> roles = roleDao.getRoles(employee);
        return allowedDestinationService.allowedDestinationsFor(employee, possibleLocations, tempRchs, roles);
    }
}
