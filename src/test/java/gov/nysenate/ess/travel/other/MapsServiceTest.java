package gov.nysenate.ess.travel.other;

import gov.nysenate.ess.core.BaseTest;
import gov.nysenate.ess.core.model.unit.Address;
import gov.nysenate.ess.travel.maps.MapsService;
import gov.nysenate.ess.web.SillyTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Category(SillyTest.class)
public class MapsServiceTest extends BaseTest {
    @Autowired
    MapsService mapsService;

    Address origin = new Address("515 Loudon Road Loudonville, NY, 12211");

    @Test
    public void testGetDistance() {
        ArrayList<Address> destinations = new ArrayList<>();
        destinations.add(new Address("515 Loudon Road Loudonville, NY, 12211"));
        destinations.add(new Address("Bombers Burrito Bar, 258 Lark St, Albany, NY 12210"));
        destinations.add(new Address("Times Union Center, 51 S Pearl St, Albany, NY 12207"));
        destinations.add(new Address("515 Loudon Road Loudonville, NY, 12211"));
        mapsService.getTripDistance(destinations);
    }
}
