package gov.nysenate.ess.travel.other;

import gov.nysenate.ess.core.BaseTest;
import gov.nysenate.ess.core.model.unit.Address;
import gov.nysenate.ess.travel.maps.MapsService;
import gov.nysenate.ess.web.SillyTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

@Category(SillyTest.class)
public class MapsServiceTest extends BaseTest {
    @Autowired
    MapsService mapsService;

    Address origin = new Address("515 Loudon Road Loudonville, NY, 12211");
    List<Address> destinations = Arrays.asList(new Address[] {
            new Address("515 Loudon Road Loudonville, NY, 12211"),
            new Address("Bombers Burrito Bar, 258 Lark St, Albany, NY 12210"),
            new Address("Times Union Center, 51 S Pearl St, Albany, NY 12207"),
            new Address("515 Loudon Road Loudonville, NY, 12211")
    });

    @Test
    public void testGetDistance() {
        mapsService.getTripDistance(destinations);
    }
}
