package gov.nysenate.ess.travel;

import gov.nysenate.ess.core.annotation.UnitTest;
import gov.nysenate.ess.core.model.unit.Address;
import gov.nysenate.ess.travel.application.model.GsaAllowance;
import gov.nysenate.ess.travel.application.model.Itinerary;
import gov.nysenate.ess.travel.application.model.ModeOfTransportation;
import gov.nysenate.ess.travel.application.model.TravelDestination;
import gov.nysenate.ess.travel.gsa.GsaAllowanceService;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

@Category(UnitTest.class)
public class GsaAllowanceServiceTest {

    @Test
    public void testBaseFunctionality() {
        Address fromAddress = new Address("515 Loudon Rd", "Loudonville", "NY", "12211");
        Address toAddress = new Address("S Mall Arterial", "Albany", "NY", "12210");

        LocalDateTime arrival = LocalDateTime.of(2017, Month.SEPTEMBER, 30, 20, 0);
        LocalDateTime departure = LocalDateTime.of(2017, Month.OCTOBER, 2, 8, 0);

        ModeOfTransportation modeOfTransportation = ModeOfTransportation.PERSONAL_AUTO;

        TravelDestination travelDestination = new TravelDestination(arrival, departure, toAddress, modeOfTransportation);
        List<TravelDestination> travelDestinations = Arrays.asList(travelDestination);

        GsaAllowanceService gsaAllowanceService = new GsaAllowanceService();
        Itinerary itinerary = new Itinerary(fromAddress, travelDestinations);
        GsaAllowance gsaAllowance = gsaAllowanceService.computeAllowance(itinerary);
        assertEquals(gsaAllowance.getMeals().toBigInteger().intValueExact(), 13 + 26 + 13);
        assertEquals(gsaAllowance.getLodging().toBigInteger().intValueExact(), 116 + 115);
        assertEquals(gsaAllowance.getIncidental().toBigInteger().intValueExact(), 0);
    }
}
