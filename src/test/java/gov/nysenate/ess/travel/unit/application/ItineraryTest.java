package gov.nysenate.ess.travel.unit.application;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import gov.nysenate.ess.core.annotation.UnitTest;
import gov.nysenate.ess.core.model.unit.Address;
import gov.nysenate.ess.travel.allowance.mileage.model.Leg;
import gov.nysenate.ess.travel.allowance.mileage.model.Route;
import gov.nysenate.ess.travel.application.model.Itinerary;
import gov.nysenate.ess.travel.application.model.ModeOfTransportation;
import gov.nysenate.ess.travel.application.model.TravelDestination;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;

@Category(UnitTest.class)
public class ItineraryTest {

    private Address validAddress = new Address("101 Washington Ave", "Albany", "NY", "12210");
    private List<TravelDestination> initializedDestinations;
    private Address eagleStreet = new Address("24 Eagle Street", "Albany", "NY", "12207");
    private Address southLakeStreet = new Address("100 South Lake Street", "Albany", "NY", "12208");
    private static ModeOfTransportation personalAuto = ModeOfTransportation.PERSONAL_AUTO;

    @Before
    public void before() {
       initializedDestinations = new ArrayList<>();
       initializedDestinations.add(new TravelDestination(LocalDate.now(), LocalDate.now(), eagleStreet, personalAuto));
    }

    /** --- Constructor Tests --- */

    @Test (expected = NullPointerException.class)
    public void nullOrigin_isInvalid() {
        new Itinerary(null, initializedDestinations);
    }

    @Test (expected = NullPointerException.class)
    public void nullDestinationList_isInvalid() {
        new Itinerary(validAddress, null);
    }

    @Test (expected = IllegalArgumentException.class)
    public void emptyOrigin_isInvalid() {
        new Itinerary(new Address(), initializedDestinations);
    }

    @Test (expected = IllegalArgumentException.class)
    public void emptyDestinationList_isInvalid() {
        new Itinerary(validAddress, new ArrayList<>());
    }

    /** --- Method Tests --- */

    @Test
    public void calculateSingleDestinationRoute() {
        TravelDestination dest = new TravelDestination(LocalDate.now(), LocalDate.now(), eagleStreet, personalAuto);
        Itinerary itinerary = new Itinerary(validAddress, Lists.newArrayList(dest));
        Route actual = itinerary.getReimbursableRoute();
        Route expected = new Route(Sets.newHashSet(new Leg(validAddress, eagleStreet)),
                Sets.newHashSet(new Leg(eagleStreet, validAddress)));
        assertEquals(expected, actual);
    }

    @Test
    public void calculateMultiDestinationRoute() {
        TravelDestination dest1 = new TravelDestination(LocalDate.now(), LocalDate.now(), eagleStreet, personalAuto);
        TravelDestination dest2 = new TravelDestination(LocalDate.now(), LocalDate.now(), southLakeStreet, personalAuto);
        Itinerary itinerary = new Itinerary(validAddress, Lists.newArrayList(dest1, dest2));
        Route actual = itinerary.getReimbursableRoute();

        Set<Leg> expectedOutbound = Sets.newHashSet(new Leg(validAddress, eagleStreet), new Leg(eagleStreet, southLakeStreet));
        Set<Leg> expectedReturn = Sets.newHashSet(new Leg(southLakeStreet, validAddress));
        Route expected = new Route(expectedOutbound, expectedReturn);
        assertEquals(expected, actual);
    }

    @Test
    public void canComputeTripStartAndEndDates() {
        TravelDestination dest = new TravelDestination(LocalDate.now(), LocalDate.now(), eagleStreet, personalAuto);
        Itinerary itinerary = new Itinerary(validAddress, Lists.newArrayList(dest));
        assertEquals(LocalDate.now(), itinerary.startDate());
        assertEquals(LocalDate.now(), itinerary.endDate());

        TravelDestination dest1 = new TravelDestination(LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(3), eagleStreet, personalAuto);
        TravelDestination dest2 = new TravelDestination(LocalDate.now().plusDays(3),
                LocalDate.now().plusDays(5), southLakeStreet, personalAuto);
        itinerary = new Itinerary(validAddress, Lists.newArrayList(dest1, dest2));
        assertEquals(LocalDate.now().plusDays(1), itinerary.startDate());
        assertEquals(LocalDate.now().plusDays(5), itinerary.endDate());
    }
}
