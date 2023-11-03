package gov.nysenate.ess.supply.unit.destination;

import com.google.common.collect.Sets;
import gov.nysenate.ess.core.annotation.UnitTest;
import gov.nysenate.ess.core.model.auth.EssRole;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.model.personnel.ResponsibilityCenter;
import gov.nysenate.ess.core.model.personnel.ResponsibilityHead;
import gov.nysenate.ess.core.model.unit.Address;
import gov.nysenate.ess.core.model.unit.Location;
import gov.nysenate.ess.core.model.unit.LocationId;
import gov.nysenate.ess.supply.destination.AllowedDestinationService;
import org.junit.Before;
import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.assertEquals;

@org.junit.experimental.categories.Category(UnitTest.class)
public class AllowedDestinationServiceTest {

    private final AllowedDestinationService adService = new AllowedDestinationService();

    private Employee employee;
    private Location empWorkLocation;
    private Location empRchLocation;
    private ResponsibilityHead empRch;
    private Location tempRchLoc;
    private ResponsibilityHead tempRch;
    private final Location randomLocation = new Location(LocationId.ofString("FOOBAR-W"),
            new Address(), new ResponsibilityHead(), "", true);

    @Before
    public void before() {
        empRch = new ResponsibilityHead();
        empRch.setActive(true);
        empRch.setCode("STSBAC");

        ResponsibilityCenter empRc = new ResponsibilityCenter();
        empRc.setActive(true);
        empRc.setHead(empRch);

        empWorkLocation = new Location(LocationId.ofString("A42FB-W"), new Address(), new ResponsibilityHead(), "", true);
        empRchLocation = new Location(LocationId.ofString("A400-W"), new Address(), empRch, "", true);

        employee = new Employee();
        employee.setWorkLocation(empWorkLocation);
        employee.setRespCenter(empRc);

        tempRch = new ResponsibilityHead();
        tempRch.setActive(true);
        tempRch.setCode("STUDFELLOW");
        tempRchLoc = new Location(LocationId.ofString("TRCH-W"), new Address(), tempRch, "", true);
    }

    @Test(expected = NullPointerException.class)
    public void nullEmp_throwsNpe() {
        adService.allowedDestinationsFor(null, Sets.newHashSet(), Sets.newHashSet(), Sets.newHashSet());
    }

    @Test
    public void workLocationInsideRch_allowed() {
        Location workLoc = new Location(LocationId.ofString("A42FB-W"), new Address(), empRch, "", true);
        employee.setWorkLocation(workLoc);

        Set<Location> actualDestinations = adService.allowedDestinationsFor(employee,
                Sets.newHashSet(workLoc, randomLocation),
                Sets.newHashSet(),
                Sets.newHashSet());
        Set<Location> expectedDestinations = Sets.newHashSet(workLoc);
        assertEquals(expectedDestinations, actualDestinations);
    }

    @Test
    public void workLocationOutsideRch_allowed() {
        Location workLoc = new Location(LocationId.ofString("A42FB-W"), new Address(), tempRch, "", true);
        employee.setWorkLocation(workLoc);

        Set<Location> actualDestinations = adService.allowedDestinationsFor(employee,
                Sets.newHashSet(workLoc, randomLocation),
                Sets.newHashSet(),
                Sets.newHashSet());
        Set<Location> expectedDestinations = Sets.newHashSet(workLoc);
        assertEquals(expectedDestinations, actualDestinations);
    }

    @Test
    public void workLocationOutsideRchAndInactive_notAllowed() {
        Location workLoc = new Location(LocationId.ofString("A42FB-W"), new Address(), tempRch, "", false);
        employee.setWorkLocation(workLoc);

        Set<Location> actualDestinations = adService.allowedDestinationsFor(employee,
                Sets.newHashSet(workLoc, randomLocation),
                Sets.newHashSet(),
                Sets.newHashSet());
        Set<Location> expectedDestinations = Sets.newHashSet();
        assertEquals(expectedDestinations, actualDestinations);
    }

    @Test
    public void workLocationInsideRchAndInactive_notAllowed() {
        Location workLoc = new Location(LocationId.ofString("A42FB-W"), new Address(), empRch, "", false);
        employee.setWorkLocation(workLoc);

        Set<Location> actualDestinations = adService.allowedDestinationsFor(employee,
                Sets.newHashSet(workLoc, randomLocation),
                Sets.newHashSet(),
                Sets.newHashSet());
        Set<Location> expectedDestinations = Sets.newHashSet();
        assertEquals(expectedDestinations, actualDestinations);
    }

    @Test
    public void workLocationNull_notAllowed() {
        Location workLoc = null;
        employee.setWorkLocation(workLoc);

        Set<Location> actualDestinations = adService.allowedDestinationsFor(employee,
                Sets.newHashSet(randomLocation),
                Sets.newHashSet(),
                Sets.newHashSet());
        Set<Location> expectedDestinations = Sets.newHashSet();
        assertEquals(expectedDestinations, actualDestinations);
    }

    @Test
    public void workLocationNotWorkType_notAllowed() {
        Location workLoc = new Location(LocationId.ofString("A42FB-P"), new Address(), tempRch, "", true);
        employee.setWorkLocation(workLoc);

        Set<Location> actualDestinations = adService.allowedDestinationsFor(employee,
                Sets.newHashSet(workLoc, randomLocation),
                Sets.newHashSet(),
                Sets.newHashSet());
        Set<Location> expectedDestinations = Sets.newHashSet();
        assertEquals(expectedDestinations, actualDestinations);
    }

    @Test
    public void workLocationTemporary_notAllowed() {
        Location workLoc = new Location(LocationId.ofString("TEMP40-W"), new Address(), tempRch, "", true);
        employee.setWorkLocation(workLoc);

        Set<Location> actualDestinations = adService.allowedDestinationsFor(employee,
                Sets.newHashSet(workLoc, randomLocation),
                Sets.newHashSet(),
                Sets.newHashSet());
        Set<Location> expectedDestinations = Sets.newHashSet();
        assertEquals(expectedDestinations, actualDestinations);
    }

    @Test
    public void ignoreInactiveEmployeeRch() {
        employee.getRespCenter().getHead().setActive(false);

        Set<Location> actualDestinations = adService.allowedDestinationsFor(employee,
                Sets.newHashSet(empWorkLocation, empRchLocation, randomLocation),
                Sets.newHashSet(),
                Sets.newHashSet());
        Set<Location> expectedDestinations = Sets.newHashSet(empWorkLocation);
        assertEquals(expectedDestinations, actualDestinations);
    }

    @Test
    public void ignoreNullEmployeeRch() {
        employee.getRespCenter().setHead(null);

        Set<Location> actualDestinations = adService.allowedDestinationsFor(employee,
                Sets.newHashSet(empWorkLocation, empRchLocation, randomLocation),
                Sets.newHashSet(),
                Sets.newHashSet());
        Set<Location> expectedDestinations = Sets.newHashSet(empWorkLocation);
        assertEquals(expectedDestinations, actualDestinations);
    }

    @Test
    public void ignoreRcIfInactive() {
        employee.getRespCenter().setActive(false);

        Set<Location> actualDestinations = adService.allowedDestinationsFor(employee,
                Sets.newHashSet(empWorkLocation, empRchLocation, randomLocation),
                Sets.newHashSet(),
                Sets.newHashSet());
        Set<Location> expectedDestinations = Sets.newHashSet(empWorkLocation);
        assertEquals(expectedDestinations, actualDestinations);
    }

    @Test
    public void ignoreRcIfNull() {
        employee.setRespCenter(null);

        Set<Location> actualDestinations = adService.allowedDestinationsFor(employee,
                Sets.newHashSet(empWorkLocation, empRchLocation, randomLocation),
                Sets.newHashSet(),
                Sets.newHashSet());
        Set<Location> expectedDestinations = Sets.newHashSet(empWorkLocation);
        assertEquals(expectedDestinations, actualDestinations);
    }

    @Test
    public void includeLocationsInEmployeeRch() {
        Set<Location> actualDestinations = adService.allowedDestinationsFor(employee,
                Sets.newHashSet(empWorkLocation, empRchLocation, randomLocation),
                Sets.newHashSet(),
                Sets.newHashSet());
        Set<Location> expectedDestinations = Sets.newHashSet(empWorkLocation, empRchLocation);
        assertEquals(expectedDestinations, actualDestinations);
    }

    @Test
    public void includeLocationsInTempRch() {
        Set<Location> actualDestinations = adService.allowedDestinationsFor(employee,
                Sets.newHashSet(empWorkLocation, empRchLocation, tempRchLoc, randomLocation),
                Sets.newHashSet(tempRch),
                Sets.newHashSet());
        Set<Location> expectedDestinations = Sets.newHashSet(empWorkLocation, empRchLocation, tempRchLoc);
        assertEquals(expectedDestinations, actualDestinations);
    }

    @Test
    public void ignoreInactiveTempRch() {
        tempRch.setActive(false);

        Set<Location> actualDestinations = adService.allowedDestinationsFor(employee,
                Sets.newHashSet(empWorkLocation, empRchLocation, tempRchLoc, randomLocation),
                Sets.newHashSet(tempRch),
                Sets.newHashSet());
        Set<Location> expectedDestinations = Sets.newHashSet(empWorkLocation, empRchLocation);
        assertEquals(expectedDestinations, actualDestinations);
    }

    @Test
    public void ifSupplyEmployee_returnAllValidDestinations() {
        Set<Location> actualDestinations = adService.allowedDestinationsFor(employee,
                Sets.newHashSet(empWorkLocation, empRchLocation, tempRchLoc, randomLocation),
                Sets.newHashSet(tempRch),
                Sets.newHashSet(EssRole.SUPPLY_EMPLOYEE));
        Set<Location> expectedDestinations = Sets.newHashSet(empWorkLocation, empRchLocation, tempRchLoc, randomLocation);
        assertEquals(expectedDestinations, actualDestinations);
    }
}
