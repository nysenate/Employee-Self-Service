package gov.nysenate.ess.travel.other;

import gov.nysenate.ess.core.BaseTest;
import gov.nysenate.ess.core.model.unit.Address;
import gov.nysenate.ess.travel.allowance.mileage.model.Leg;
import gov.nysenate.ess.travel.application.model.ModeOfTransportation;
import gov.nysenate.ess.travel.application.model.TravelDestination;
import gov.nysenate.ess.travel.maps.GoogleMapsService;
import gov.nysenate.ess.web.SillyTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;

@Category(SillyTest.class)
public class GoogleMapsServiceTest extends BaseTest {
    @Autowired
    private GoogleMapsService googleMapsService;

    @Test
    public void testGetDistance() throws Exception {
        Address origin = new Address("515 Loudon Road Loudonville, NY, 12211");
        Address dest = new Address("Bombers Burrito Bar, 258 Lark St, Albany, NY 12210");
        long l = googleMapsService.getLegDistance(new Leg(origin, dest));
        System.out.println(l);
    }
}