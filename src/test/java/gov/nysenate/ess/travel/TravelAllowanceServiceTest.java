package gov.nysenate.ess.travel;

import com.google.maps.model.Unit;
import gov.nysenate.ess.core.annotation.UnitTest;
import gov.nysenate.ess.core.model.unit.Address;
import gov.nysenate.ess.travel.application.model.Itinerary;
import gov.nysenate.ess.travel.application.model.TransportationAllowance;
import gov.nysenate.ess.travel.application.model.TravelDestination;
import gov.nysenate.ess.travel.maps.MapsService;
import gov.nysenate.ess.travel.travelallowance.TravelAllowanceService;
import gov.nysenate.ess.web.SillyTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.internal.runners.JUnit38ClassRunner;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

@Category(UnitTest.class)
public class TravelAllowanceServiceTest {

    @Test
    public void test() {
        //travel less than 35 miles total
        ArrayList<TravelDestination> dests = new ArrayList<>();
        dests.add(new TravelDestination(LocalDate.now(), LocalDate.now(),
                new Address("515 Loudon Road", "Loudonville", "NY", "12211")));
        Itinerary itinerary = new Itinerary(new Address("100 South Swan Street", "Albany", "NY", "12210"), dests);

        TravelAllowanceService travelAllowanceService = new TravelAllowanceService();
        TransportationAllowance ta = travelAllowanceService.updateTravelAllowance(itinerary);
        assertEquals(ta.getMileage(), "0");

        //travel more than 35 miles total, but less than 35 miles in one direction


        //travel more than 35 miles from Albany
    }

}
