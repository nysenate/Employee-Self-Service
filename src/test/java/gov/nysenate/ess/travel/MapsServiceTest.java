package gov.nysenate.ess.travel;

import gov.nysenate.ess.core.BaseTest;
import gov.nysenate.ess.travel.maps.MapsService;
import gov.nysenate.ess.web.SillyTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

@Category(SillyTest.class)
public class MapsServiceTest extends BaseTest {
    @Autowired
    MapsService mapsService;

    String origin = "515 Loudon Road Loudonville, NY, 12211";
    String[] destinations = new String[] {
            "Bombers Burrito Bar, 258 Lark St, Albany, NY 12210",
            "Times Union Center, 51 S Pearl St, Albany, NY 12207"
    };

    @Test
    public void testGetDistance() {
        mapsService.getTripDistance(origin, destinations);
    }
}
