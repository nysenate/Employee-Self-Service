package gov.nysenate.ess.travel.unit.application;

import com.google.common.collect.*;
import gov.nysenate.ess.core.annotation.UnitTest;
import gov.nysenate.ess.core.model.unit.Address;
import gov.nysenate.ess.travel.allowance.mileage.model.Leg;
import gov.nysenate.ess.travel.allowance.mileage.model.Route;
import gov.nysenate.ess.travel.application.model.Itinerary;
import gov.nysenate.ess.travel.application.model.ModeOfTransportation;
import gov.nysenate.ess.travel.application.model.TravelDestination;
import gov.nysenate.ess.travel.application.model.TravelDestinationOptions;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.time.LocalDate;
import java.util.*;

import static org.junit.Assert.assertEquals;

@Category(UnitTest.class)
public class ItineraryTest {

    private static TravelDestinationOptions DEFAULT_OPTIONS = new TravelDestinationOptions(
            ModeOfTransportation.PERSONAL_AUTO).defaultAllowances();

    private static LocalDate MONDAY = LocalDate.of(2018, 1, 1);
    private static LocalDate TUESDAY = LocalDate.of(2018, 1, 2);
    private static LocalDate SATURDAY = LocalDate.of(2018, 1, 6);

    private static Address washingtonAve = new Address("101 Washington Ave", "Albany", "NY", "12210");
    private static Address eagleStreet = new Address("24 Eagle Street", "Albany", "NY", "12207");
    private static Address southLakeStreet = new Address("100 South Lake Street", "Albany", "NY", "12208");

    private Itinerary itinerary;
    private TravelDestination destination;

    @Before
    public void before() {
        itinerary = new Itinerary(washingtonAve);
        destination = new TravelDestination(MONDAY, MONDAY, eagleStreet);
    }

    /** --- Constructor Tests --- */

    @Test (expected = NullPointerException.class)
    public void nullOrigin_isInvalid() {
        new Itinerary(null);
    }

    @Test (expected = NullPointerException.class)
    public void nullDestinationList_isInvalid() {
        itinerary.addDestination(null, null);
    }

    @Test (expected = IllegalArgumentException.class)
    public void emptyOrigin_isInvalid() {
        new Itinerary(new Address());
    }

    /** --- Method Tests --- */

    @Test
    public void singleDayTrip_startAndEndDatesTheSame() {
        itinerary = itinerary.addDestination(destination, DEFAULT_OPTIONS);
        assertEquals(MONDAY, itinerary.startDate());
        assertEquals(MONDAY, itinerary.endDate());
    }

    @Test
    public void multiDayTrip_startAndEndDatesTest() {
        TravelDestination dest1 = new TravelDestination(MONDAY, TUESDAY, eagleStreet);
        TravelDestination dest2 = new TravelDestination(TUESDAY, SATURDAY, southLakeStreet);
        itinerary = itinerary.addDestination(dest1, DEFAULT_OPTIONS);
        itinerary = itinerary.addDestination(dest2, DEFAULT_OPTIONS);
        assertEquals(MONDAY, itinerary.startDate());
        assertEquals(SATURDAY, itinerary.endDate());
    }

    @Test
    public void nonPersonalAutoModeOfTrans_NotReimbursable() {
        TravelDestinationOptions options = new TravelDestinationOptions(ModeOfTransportation.AIRPLANE).requestMileage();
        itinerary = itinerary.addDestination(destination, options);
        Route actual = itinerary.getReimbursableRoute();
        Route expected = new Route(ImmutableSet.of(), ImmutableSet.of());
        assertEquals(expected, actual);
    }

    @Test
    public void notRequestingMileage_MileageNotReimbursable() {
        TravelDestinationOptions options = new TravelDestinationOptions(ModeOfTransportation.PERSONAL_AUTO);
        itinerary = itinerary.addDestination(destination, options);

        Route actual = itinerary.getReimbursableRoute();
        Route expected = new Route(ImmutableSet.of(), ImmutableSet.of());
        assertEquals(expected, actual);
    }

    @Test
    public void calculateReimbursableRoute() {
        TravelDestination dest2 = new TravelDestination(MONDAY, MONDAY, southLakeStreet);
        itinerary = itinerary.addDestination(destination, DEFAULT_OPTIONS);
        itinerary = itinerary.addDestination(dest2, DEFAULT_OPTIONS);

        Route actual = itinerary.getReimbursableRoute();

        Set<Leg> expectedOutbound = Sets.newHashSet(new Leg(washingtonAve, eagleStreet), new Leg(eagleStreet, southLakeStreet));
        Set<Leg> expectedReturn = Sets.newHashSet(new Leg(southLakeStreet, washingtonAve));
        Route expected = new Route(expectedOutbound, expectedReturn);

        assertEquals(expected, actual);
    }

    @Test
    public void testLodgingRequestedDestinations() {
        TravelDestination lodgingNotRequested = new TravelDestination(MONDAY, MONDAY, southLakeStreet);
        itinerary = itinerary.addDestination(destination, new TravelDestinationOptions(ModeOfTransportation.PERSONAL_AUTO).requestLodging());
        itinerary = itinerary.addDestination(lodgingNotRequested, new TravelDestinationOptions(ModeOfTransportation.PERSONAL_AUTO));

        Set<TravelDestination> actual = itinerary.getLodgingRequestedDestinations();
        Set<TravelDestination> expected = Sets.newHashSet(destination);

        assertEquals(expected, actual);
    }

    @Test
    public void testMealRequestedDestinations() {
        TravelDestination mealNotRequested = new TravelDestination(MONDAY, MONDAY, southLakeStreet);
        itinerary = itinerary.addDestination(destination, new TravelDestinationOptions(ModeOfTransportation.PERSONAL_AUTO).requestMeals());
        itinerary = itinerary.addDestination(mealNotRequested, new TravelDestinationOptions(ModeOfTransportation.PERSONAL_AUTO));

        Set<TravelDestination> actual = itinerary.getMealsRequestedDestinations();
        Set<TravelDestination> expected = Sets.newHashSet(destination);

        assertEquals(expected, actual);
    }
}
