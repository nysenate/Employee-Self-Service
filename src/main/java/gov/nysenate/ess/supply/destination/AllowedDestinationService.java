package gov.nysenate.ess.supply.destination;

import gov.nysenate.ess.core.model.auth.EssRole;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.model.personnel.ResponsibilityHead;
import gov.nysenate.ess.core.model.unit.Location;
import gov.nysenate.ess.core.model.unit.LocationType;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;

@Service
public class AllowedDestinationService {

    /**
     * Calculates the locations an employee is allowed to place supply orders for.
     *
     * @param employee               An employee to calculate allowed destinations for.
     * @param possibleDestinations   Locations to search through. Should usually be all active locations.
     * @param tempResponsibiltyHeads All temp responsibility Heads granted to the employee
     * @param employeeRoles          The EssRoles belonging to the given employee.
     * @return Set of Locations the employee is allowed to order for.
     */
    public Set<Location> allowedDestinationsFor(Employee employee,
                                                Collection<Location> possibleDestinations,
                                                Collection<ResponsibilityHead> tempResponsibiltyHeads,
                                                Collection<EssRole> employeeRoles) {
        checkNotNull(employee);
        checkNotNull(possibleDestinations);
        checkNotNull(tempResponsibiltyHeads);
        checkNotNull(employeeRoles);

        if (isSupplyStaff(employeeRoles)) {
            return validDestinations(possibleDestinations);
        }

        // Remove any inactive Temp RCH's
        Set<ResponsibilityHead> activeTempRchs = tempResponsibiltyHeads.stream()
                .filter(ResponsibilityHead::isActive)
                .collect(Collectors.toSet());

        return validDestinations(possibleDestinations).stream()
                .filter(loc -> isEmpWorkLocation(employee, loc)
                        || isInEmpRch(employee, loc)
                        || isInTempRch(activeTempRchs, loc))
                .collect(Collectors.toSet());
    }

    // Removes locations which are never allowed to be destinations.
    private Set<Location> validDestinations(Collection<Location> locations) {
        return locations.stream()
                .filter(Location::isActive)
                .filter(loc -> loc.getLocId().getType().equals(LocationType.WORK))
                .filter(loc -> !loc.getLocId().getCode().startsWith("TEMP"))
                .collect(Collectors.toSet());
    }

    private boolean isSupplyStaff(Collection<EssRole> employeeRoles) {
        return employeeRoles.contains(EssRole.SUPPLY_EMPLOYEE);
    }

    private boolean isEmpWorkLocation(Employee employee, Location loc) {
        return employee.getWorkLocation() != null
                && employee.getWorkLocation().isActive()
                && employee.getWorkLocation().equals(loc);
    }

    private boolean isInEmpRch(Employee employee, Location loc) {
        return employee.getRespCenter() != null
                && employee.getRespCenter().isActive()
                && employee.getRespCenter().getHead() != null
                && employee.getRespCenter().getHead().isActive()
                && loc.getResponsibilityHead().equals(employee.getRespCenter().getHead());
    }

    private boolean isInTempRch(Collection<ResponsibilityHead> tempRchs, Location loc) {
        return tempRchs.contains(loc.getResponsibilityHead());
    }
}
