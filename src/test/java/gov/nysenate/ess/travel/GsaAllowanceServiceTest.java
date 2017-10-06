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

import java.time.LocalDate;
import java.time.Month;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

@Category(UnitTest.class)
public class GsaAllowanceServiceTest {

    Address fromAddress = new Address("515 Loudon Rd", "Loudonville", "NY", "12211");
    Address toAddress = new Address("S Mall Arterial", "Albany", "NY", "12210");

    @Test
    public void testMealsAndLodging() {
        // one day
        LocalDate arrival = LocalDate.of(2017, Month.SEPTEMBER, 30);
        LocalDate departure = LocalDate.of(2017, Month.SEPTEMBER, 30);
        GsaAllowance gsaAllowance = createGsaAllowance(arrival, departure);
        assertEquals(gsaAllowance.getMeals().toBigInteger().intValueExact(), 13 + 26);

        // two days
        arrival = LocalDate.of(2017, Month.SEPTEMBER, 29);
        departure = LocalDate.of(2017, Month.SEPTEMBER, 30);
        gsaAllowance = createGsaAllowance(arrival, departure);
        assertEquals(gsaAllowance.getMeals().toBigInteger().intValueExact(), 13 + 26 + 13 + 26);
        assertEquals(gsaAllowance.getLodging().toBigInteger().intValueExact(), 116);

        // cross months (three days)
        arrival = LocalDate.of(2017, Month.AUGUST, 30);
        departure = LocalDate.of(2017, Month.SEPTEMBER, 1);
        gsaAllowance = createGsaAllowance(arrival, departure);
        assertEquals(gsaAllowance.getMeals().toBigInteger().intValueExact(), 13 + 26 + 13 + 26 + 13 + 26);
        assertEquals(gsaAllowance.getLodging().toBigInteger().intValueExact(), 116 + 116);

        // cross months (new fiscal year)
        arrival = LocalDate.of(2017, Month.SEPTEMBER, 30);
        departure = LocalDate.of(2017, Month.OCTOBER, 2);
        gsaAllowance = createGsaAllowance(arrival, departure);
        assertEquals(gsaAllowance.getMeals().toBigInteger().intValueExact(), 13 + 26 + 13 + 26 + 13 + 26);
        assertEquals(gsaAllowance.getLodging().toBigInteger().intValueExact(), 116 + 115);
    }

    private GsaAllowance createGsaAllowance(LocalDate arrival, LocalDate departure) {
        ModeOfTransportation modeOfTransportation = ModeOfTransportation.PERSONAL_AUTO;
        TravelDestination travelDestination = new TravelDestination(arrival, departure, toAddress, modeOfTransportation);
        List<TravelDestination> travelDestinations = Arrays.asList(travelDestination);

        GsaAllowanceService gsaAllowanceService = new GsaAllowanceService();
        Itinerary itinerary = new Itinerary(fromAddress, travelDestinations);
        return gsaAllowanceService.computeAllowance(itinerary);
    }
}
