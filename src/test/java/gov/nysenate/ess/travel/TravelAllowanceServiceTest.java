package gov.nysenate.ess.travel;

import gov.nysenate.ess.core.annotation.IntegrationTest;
import gov.nysenate.ess.core.model.unit.Address;
import gov.nysenate.ess.travel.application.model.Itinerary;
import gov.nysenate.ess.travel.application.model.TransportationAllowance;
import gov.nysenate.ess.travel.application.model.TravelDestination;
import gov.nysenate.ess.travel.travelallowance.TravelAllowanceService;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

@Category(IntegrationTest.class)
public class TravelAllowanceServiceTest {

    @Test
    public void lessThan35miles_allowanceEquals0() {
        //travel less than 35 miles total
        ArrayList<TravelDestination> dests = new ArrayList<>();
        dests.add(new TravelDestination(LocalDate.now(), LocalDate.now(),
                new Address("515 Loudon Road", "Loudonville", "NY", "12211")));
        Itinerary itinerary = new Itinerary(new Address("100 South Swan Street", "Albany", "NY", "12210"), dests);

        TravelAllowanceService travelAllowanceService = new TravelAllowanceService();
        TransportationAllowance ta = travelAllowanceService.updateTravelAllowance(itinerary);
        assertEquals(ta.getMileage(), "0");
    }

    @Test
    public void test2() {
        //travel more than 35 miles total, but less than 35 miles in one direction
        ArrayList<TravelDestination> dests = new ArrayList<>();
        dests.add(new TravelDestination(LocalDate.now(), LocalDate.now(),
                new Address("Skidmore College, North Broadway, Saratoga, NY")));
        Itinerary itinerary = new Itinerary(new Address("100 South Swan Street", "Albany", "NY", "12210"), dests);

        TravelAllowanceService travelAllowanceService = new TravelAllowanceService();
        TransportationAllowance ta = travelAllowanceService.updateTravelAllowance(itinerary);
        assertEquals(ta.getMileage(), "0");
    }

    @Test
    public void moreThan35miles() {
    //travel more than 35 miles from Albany
    ArrayList<TravelDestination> dests = new ArrayList<>();
    dests.add(new TravelDestination(LocalDate.now(), LocalDate.now(),
            new Address("181 Fort Edward Road", "Fort Edward", "NY", "12828")));
    Itinerary itinerary = new Itinerary(new Address("100 South Swan Street", "Albany", "NY", "12210"), dests);

    TravelAllowanceService travelAllowanceService = new TravelAllowanceService();
    TransportationAllowance ta = travelAllowanceService.updateTravelAllowance(itinerary);
    assertEquals(ta.getMileage(), "29.1575");

    //TODO the distance given by google maps may change based on traffic conditions
    }

}
