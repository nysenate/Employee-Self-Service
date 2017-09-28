package gov.nysenate.ess.travel;

import gov.nysenate.ess.core.annotation.UnitTest;
import gov.nysenate.ess.core.model.unit.Address;
import gov.nysenate.ess.travel.application.model.Itinerary;
import gov.nysenate.ess.travel.application.model.TravelDestination;
import gov.nysenate.ess.travel.gsa.GsaAllowanceService;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Arrays;
import java.util.List;

@Category(UnitTest.class)
public class GsaAllowanceServiceTest {

    @Test
    public void something() {
        Address fromAddress = new Address("515 Loudon Rd", "Loudonville", "NY", "12211");
        Address toAddress = new Address("S Mall Arterial", "Albany", "NY", "12210");

        LocalDateTime arrival = LocalDateTime.of(2017, Month.SEPTEMBER, 1, 19, 30);
        LocalDateTime departure = LocalDateTime.now();
        TravelDestination travelDestination = new TravelDestination(arrival, departure, toAddress);
        List<TravelDestination> travelDestinations = Arrays.asList(travelDestination);

        GsaAllowanceService gsaAllowanceService = new GsaAllowanceService();
        Itinerary itinerary = new Itinerary(fromAddress, travelDestinations);
        gsaAllowanceService.computeAllowance(itinerary);
    }
}
