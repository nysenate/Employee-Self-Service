package gov.nysenate.ess.travel.unit.authorization.permission;

import com.google.common.collect.Lists;
import gov.nysenate.ess.core.annotation.UnitTest;
import gov.nysenate.ess.travel.authorization.permission.TravelPermissionBuilder;
import gov.nysenate.ess.travel.authorization.permission.TravelPermissionObject;
import org.apache.shiro.authz.permission.WildcardPermission;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.web.bind.annotation.RequestMethod;

import static junit.framework.TestCase.assertEquals;

@Category(UnitTest.class)
public class TravelPermissionBuilderTest {

    @Test(expected = IllegalArgumentException.class)
    public void givenIsForAllEmpsFalse_thenEmpIdsCannotBeEmpty() {
        new TravelPermissionBuilder()
                .forObject(TravelPermissionObject.TRAVEL_APPLICATION)
                .forAction(RequestMethod.GET)
                .buildPermission();
    }

    @Test(expected = IllegalArgumentException.class)
    public void objectsCannotBeEmpty() {
        new TravelPermissionBuilder()
                .forEmpId(1)
                .forAction(RequestMethod.GET)
                .buildPermission();
    }

    @Test(expected = IllegalArgumentException.class)
    public void actionsCannotBeEmpty() {
        new TravelPermissionBuilder()
                .forEmpId(153432)
                .forObject(TravelPermissionObject.TRAVEL_APPLICATION)
                .buildPermission();
    }

    @Test
    public void constructsSimplePermission() {
        WildcardPermission actual = new TravelPermissionBuilder()
                .forEmpId(1)
                .forObject(TravelPermissionObject.TRAVEL_APPLICATION)
                .forAction(RequestMethod.GET)
                .buildPermission();
        WildcardPermission expected = new WildcardPermission("travel:TRAVEL_APPLICATION:1:GET");
        assertEquals(expected, actual);
    }

    @Test
    public void empIdsUsesMultipleParts() {
        WildcardPermission actual = new TravelPermissionBuilder()
                .forEmpId(1)
                .forEmpId(2)
                .forEmpId(3)
                .forEmpId(4)
                .forObject(TravelPermissionObject.TRAVEL_APPLICATION)
                .forAction(RequestMethod.GET)
                .buildPermission();
        WildcardPermission expected = new WildcardPermission("travel:TRAVEL_APPLICATION:1,2,3,4:GET");
        assertEquals(expected, actual);
    }

    @Test
    public void canGiveCollectionOfEmpIds() {
        WildcardPermission actual = new TravelPermissionBuilder()
                .forEmpIds(Lists.newArrayList(1, 2, 3, 4))
                .forObject(TravelPermissionObject.TRAVEL_APPLICATION)
                .forAction(RequestMethod.GET)
                .buildPermission();
        WildcardPermission expected = new WildcardPermission("travel:TRAVEL_APPLICATION:1,2,3,4:GET");
        assertEquals(expected, actual);
    }

    @Test
    public void objectsUsesMultipleParts() {
        WildcardPermission actual = new TravelPermissionBuilder()
                .forEmpId(1)
                .forObject(TravelPermissionObject.TRAVEL_APPLICATION)
                .forObject(TravelPermissionObject.TRAVEL_APPLICATION_REVIEW)
                .forAction(RequestMethod.GET)
                .buildPermission();
        WildcardPermission expected = new WildcardPermission("travel:TRAVEL_APPLICATION,TRAVEL_APPLICATION_REVIEW:1:GET");
        assertEquals(expected, actual);
    }

    @Test
    public void actionsUsesMultipleParts() {
        WildcardPermission actual = new TravelPermissionBuilder()
                .forEmpId(1)
                .forObject(TravelPermissionObject.TRAVEL_APPLICATION)
                .forAction(RequestMethod.GET)
                .forAction(RequestMethod.POST)
                .buildPermission();
        WildcardPermission expected = new WildcardPermission("travel:TRAVEL_APPLICATION:1:GET,POST");
        assertEquals(expected, actual);
    }

    @Test
    public void testMultiplePartsForAllParts() {
        WildcardPermission actual = new TravelPermissionBuilder()
                .forEmpId(1)
                .forEmpId(2)
                .forEmpId(3)
                .forObject(TravelPermissionObject.TRAVEL_APPLICATION)
                .forObject(TravelPermissionObject.TRAVEL_APPLICATION_REVIEW)
                .forAction(RequestMethod.GET)
                .forAction(RequestMethod.POST)
                .buildPermission();
        WildcardPermission expected = new WildcardPermission("travel:TRAVEL_APPLICATION,TRAVEL_APPLICATION_REVIEW:1,2,3:GET,POST");
        assertEquals(expected, actual);
    }

    @Test
    public void testEmpIdWildcard() {
        WildcardPermission actual = new TravelPermissionBuilder()
                .forAllEmps()
                .forObject(TravelPermissionObject.TRAVEL_APPLICATION)
                .forAction(RequestMethod.GET)
                .buildPermission();
        WildcardPermission expected = new WildcardPermission("travel:TRAVEL_APPLICATION:*:GET");
        assertEquals(expected, actual);
    }

    @Test
    public void givenAccidentalDuplicates_ignoreThem() {
        // This is handled by Shiro WildcardPermission.
        WildcardPermission actual = new TravelPermissionBuilder()
                .forEmpId(1)
                .forEmpId(1)
                .forEmpId(2)
                .forObject(TravelPermissionObject.TRAVEL_APPLICATION)
                .forObject(TravelPermissionObject.TRAVEL_APPLICATION)
                .forAction(RequestMethod.GET)
                .forAction(RequestMethod.GET)
                .buildPermission();
        WildcardPermission expected = new WildcardPermission("travel:TRAVEL_APPLICATION:1,2:GET");
        assertEquals(expected, actual);
    }
}
