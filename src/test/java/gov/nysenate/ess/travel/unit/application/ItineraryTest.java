package gov.nysenate.ess.travel.unit.application;

import com.google.common.collect.Lists;
import gov.nysenate.ess.core.annotation.UnitTest;
import gov.nysenate.ess.core.model.unit.Address;
import gov.nysenate.ess.travel.application.model.Itinerary;
import gov.nysenate.ess.travel.application.model.TravelDestination;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

@Category(UnitTest.class)
public class ItineraryTest {

    private Address validAddress = new Address("101 Washington Ave", "Albany", "NY", "12210");
    private List<TravelDestination> validDestinations;
    private Address eagleStreet = new Address("24 Eagle Street", "Albany", "NY", "12207");
    private Address southLakeStreet = new Address("100 South Lake Street", "Albany", "NY", "12208");

    @Before
    public void before() {
       validDestinations = new ArrayList<>();
       validDestinations.add(new TravelDestination(LocalDate.now(), LocalDate.now(), validAddress));
    }

    /** --- Constructor Tests --- */

    @Test (expected = NullPointerException.class)
    public void nullOrigin_isInvalid() {
        new Itinerary(null, validDestinations);
    }

    @Test (expected = NullPointerException.class)
    public void nullDestinationList_isInvalid() {
        new Itinerary(validAddress, null);
    }

    @Test (expected = IllegalArgumentException.class)
    public void emptyOrigin_isInvalid() {
        new Itinerary(new Address(), validDestinations);
    }

    @Test (expected = IllegalArgumentException.class)
    public void emptyDestinationList_isInvalid() {
        new Itinerary(validAddress, new ArrayList<>());
    }

    /** --- Method Tests --- */

    @Test
    public void canComputeFullRouteOfTravel() {
        TravelDestination dest1 = new TravelDestination(LocalDate.now(), LocalDate.now(), eagleStreet);
        TravelDestination dest2 = new TravelDestination(LocalDate.now(), LocalDate.now(), southLakeStreet);

        Itinerary itinerary = new Itinerary(validAddress, Lists.newArrayList(dest1, dest2));
        List<Address> actualRoute = itinerary.travelRoute();
        List<Address> expectedRoute = Lists.newArrayList(validAddress, eagleStreet, southLakeStreet, validAddress);
        assertEquals(expectedRoute, actualRoute);
    }

    @Test
    public void canComputeTripStartAndEndDates() {
        Itinerary itinerary = new Itinerary(validAddress, validDestinations);
        assertEquals(LocalDate.now(), itinerary.startDate());
        assertEquals(LocalDate.now(), itinerary.endDate());

        TravelDestination dest1 = new TravelDestination(LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(3), eagleStreet);
        TravelDestination dest2 = new TravelDestination(LocalDate.now().plusDays(3),
                LocalDate.now().plusDays(5), southLakeStreet);
        itinerary = new Itinerary(validAddress, Lists.newArrayList(dest1, dest2));
        assertEquals(LocalDate.now().plusDays(1), itinerary.startDate());
        assertEquals(LocalDate.now().plusDays(5), itinerary.endDate());
    }
}
