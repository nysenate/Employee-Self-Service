package gov.nysenate.ess.travel.unit;

import com.google.common.collect.Lists;
import gov.nysenate.ess.core.annotation.UnitTest;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.travel.accommodation.Accommodation;
import gov.nysenate.ess.travel.utils.Dollars;
import gov.nysenate.ess.travel.route.Route;
import gov.nysenate.ess.travel.application.TravelApplication;
import gov.nysenate.ess.travel.fixtures.AccommodationFixture;
import gov.nysenate.ess.travel.fixtures.RouteFixture;
import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;

@UnitTest
public class TravelApplicationTest {

    @Test
    public void startAndEndDates() {
        TravelApplication app = testTravelApp();
        assertEquals(LocalDate.of(2018, 1, 1), app.startDate());
        assertEquals(LocalDate.of(2018, 1, 2), app.endDate());
    }

    @Test
    public void lodgingAllowanceSumsAllAccommodations() {
        TravelApplication app = testTravelApp();
        assertEquals(new Dollars("240"), app.lodgingAllowance());
    }

    @Test
    public void mealAllowanceSumsAllAccommodations() {
        TravelApplication app = testTravelApp();
        assertEquals(new Dollars("204"), app.mealAllowance());
    }

    @Test
    public void mileageAllowance() {
        TravelApplication app = testTravelApp();
        assertEquals(new Dollars("218"), app.mileageAllowance());
    }

    @Test
    public void totalAllowance() {
        TravelApplication app = testTravelApp();
        app.setTolls(new Dollars("46.25"));
        app.setParking(new Dollars("20.00"));
        app.setAlternate(new Dollars("5.00"));
        app.setRegistration(new Dollars("40.00"));

        assertEquals(new Dollars("773.25"), app.totalAllowance());
    }

    private TravelApplication testTravelApp() {
        Accommodation a1 = AccommodationFixture.twoDayOneNightAccommodation();
        Accommodation a2 = AccommodationFixture.twoDayOneNightAccommodation();
        Route route = RouteFixture.longOneDestinationRoute();
        TravelApplication app = new TravelApplication(0, new Employee(), new Employee());
        app.setAccommodations(Lists.newArrayList(a1, a2));
        app.setRoute(route);
        return app;
    }
}
