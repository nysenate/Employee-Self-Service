package gov.nysenate.ess.travel.application;

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

@Category(UnitTest.class)
public class ItineraryTest {

    private Address validAddress = new Address("101 Washington Ave", "Albany", "NY", "12210");
    private List<TravelDestination> validDestinations;

    @Before
    public void before() {
       validDestinations = new ArrayList<>();
       validDestinations.add(new TravelDestination(LocalDate.now(), LocalDate.now(), validAddress));
    }

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
}
