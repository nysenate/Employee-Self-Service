package gov.nysenate.ess.travel.other;

import gov.nysenate.ess.core.BaseTest;
import gov.nysenate.ess.core.model.unit.Address;
import gov.nysenate.ess.travel.maps.OsrmService;
import gov.nysenate.ess.web.SillyTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;

@Category(SillyTest.class)
public class OsrmServiceTest extends BaseTest {
    @Autowired
    OsrmService osrmService;

    @Test
    public void testGetDistance() {
        ArrayList<Address> destinations = new ArrayList<>();
        destinations.add(new Address("515 Loudon Road Loudonville, NY, 12211"));
        destinations.add(new Address("Bombers Burrito Bar, 258 Lark St, Albany, NY 12210"));
        destinations.add(new Address("Chicken Joe's Albany, 486 Yates St, Albany, NY 12208"));
        destinations.add(new Address("515 Loudon Road Loudonville, NY, 12211"));
        System.out.println(osrmService.getTripDistance(destinations).getTripDistanceTotal());
    }
}
