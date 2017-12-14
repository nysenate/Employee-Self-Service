package gov.nysenate.ess.travel.integration.allowances;

import gov.nysenate.ess.core.BaseTest;
import gov.nysenate.ess.core.annotation.IntegrationTest;
import gov.nysenate.ess.core.model.unit.Address;
import gov.nysenate.ess.travel.allowance.gsa.model.GsaAllowance;
import gov.nysenate.ess.travel.application.model.Itinerary;
import gov.nysenate.ess.travel.application.model.ModeOfTransportation;
import gov.nysenate.ess.travel.application.model.TravelDestination;
import gov.nysenate.ess.travel.allowance.gsa.service.GsaAllowanceService;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.Month;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

@Category(IntegrationTest.class)
public class GsaAllowanceServiceTest extends BaseTest {

    @Autowired private GsaAllowanceService gsaAllowanceService;

    Address fromAddress = new Address("515 Loudon Rd", "Loudonville", "NY", "12211");
    Address toAddress = new Address("S Mall Arterial", "Albany", "NY", "12210");

    @Test
    public void testMealsAndLodging() {
        // one day
        LocalDate arrival = LocalDate.of(2017, Month.SEPTEMBER, 30);
        LocalDate departure = LocalDate.of(2017, Month.SEPTEMBER, 30);
        GsaAllowance gsaAllowance = createGsaAllowance(arrival, departure);
        assertEquals(gsaAllowance.getMeals().toBigInteger().intValueExact(), 13 + 26);

        // gsa.gov is blocking us. lets limit requests.
//        // two days
//        arrival = LocalDate.of(2017, Month.SEPTEMBER, 29);
//        departure = LocalDate.of(2017, Month.SEPTEMBER, 30);
//        gsaAllowance = createGsaAllowance(arrival, departure);
//        assertEquals(gsaAllowance.getMeals().toBigInteger().intValueExact(), 13 + 26 + 13 + 26);
//        assertEquals(gsaAllowance.getLodging().toBigInteger().intValueExact(), 116);
//
//        // cross months (three days)
//        arrival = LocalDate.of(2017, Month.AUGUST, 30);
//        departure = LocalDate.of(2017, Month.SEPTEMBER, 1);
//        gsaAllowance = createGsaAllowance(arrival, departure);
//        assertEquals(gsaAllowance.getMeals().toBigInteger().intValueExact(), 13 + 26 + 13 + 26 + 13 + 26);
//        assertEquals(gsaAllowance.getLodging().toBigInteger().intValueExact(), 116 + 116);
//
//        // cross months (new fiscal year)
//        arrival = LocalDate.of(2017, Month.SEPTEMBER, 30);
//        departure = LocalDate.of(2017, Month.OCTOBER, 2);
//        gsaAllowance = createGsaAllowance(arrival, departure);
//        assertEquals(gsaAllowance.getMeals().toBigInteger().intValueExact(), 13 + 26 + 13 + 26 + 13 + 26);
//        assertEquals(gsaAllowance.getLodging().toBigInteger().intValueExact(), 116 + 115);
    }

    // TODO Tests for multiple destinations (destinations should have different rates)

    private GsaAllowance createGsaAllowance(LocalDate arrival, LocalDate departure) {
        TravelDestination travelDestination = new TravelDestination(arrival, departure, toAddress, ModeOfTransportation.PERSONAL_AUTO);
        List<TravelDestination> travelDestinations = Arrays.asList(travelDestination);

        Itinerary itinerary = new Itinerary(fromAddress, travelDestinations);
//        return gsaAllowanceService.computeAllowance(itinerary);
        return null;
    }
}
