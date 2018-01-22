package gov.nysenate.ess.travel.unit.application;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import gov.nysenate.ess.core.annotation.UnitTest;
import gov.nysenate.ess.core.model.unit.Address;
import gov.nysenate.ess.travel.application.model.ModeOfTransportation;
import gov.nysenate.ess.travel.application.model.TravelDestination;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;

@Category(UnitTest.class)
public class TravelDestinationTest {

    private LocalDate validDate = LocalDate.now();
    private Address validAddress = new Address("101 Washington Ave", "Albany", "NY", "12210");

    /** --- Constructor Tests --- */

    @Test (expected = NullPointerException.class)
    public void nullArrivalDate_isInvalid() {
        new TravelDestination(null, validDate, validAddress);
    }

    @Test (expected = NullPointerException.class)
    public void nullDepartureDate_isInvalid() {
        new TravelDestination(validDate, null, validAddress);
    }

    @Test (expected = NullPointerException.class)
    public void nullAddress_isInvalid() {
        new TravelDestination(validDate, validDate, null);
    }

    @Test (expected = IllegalArgumentException.class)
    public void emptyAddress_isInvalid() {
        new TravelDestination(validDate, validDate, new Address());
    }

    @Test (expected = IllegalArgumentException.class)
    public void departureDateBeforeArrivalDate_isInvalid() {
        new TravelDestination(LocalDate.now(), LocalDate.now().minusDays(1), validAddress);
    }

    @Test
    public void arrivalDateEqualsDepartureDate_valid() {
        new TravelDestination(LocalDate.now(), LocalDate.now(), validAddress);
    }

    /** --- Method Tests --- */

    @Test
    public void singleDayStay_hasOneDayOfStay() {
        TravelDestination destination = new TravelDestination(validDate, validDate, validAddress);
        List actual = destination.getDatesOfStay();
        List expected = Lists.newArrayList(validDate);
        assertEquals(expected, actual);
    }

    @Test
    public void weekStay_daysOfStayIncludeEntireWeek() {
        TravelDestination destination = new TravelDestination(validDate, validDate.plusDays(7), validAddress);
        List actual = destination.getDatesOfStay();
        List expected = Lists.newArrayList(validDate, validDate.plusDays(1), validDate.plusDays(2),
                validDate.plusDays(3),validDate.plusDays(4), validDate.plusDays(5), validDate.plusDays(6),
                validDate.plusDays(7));
        assertEquals(expected, actual);
    }

    @Test
    public void singleDayStay_hasNoNightsOfStay() {
        TravelDestination destination = new TravelDestination(validDate, validDate, validAddress);
        Set actual = destination.getNightsOfStay();
        Set expected = Sets.newHashSet();
        assertEquals(expected, actual);
    }

    @Test
    public void overNightStay_hasSingleNightOfStay() {
        TravelDestination destination = new TravelDestination(validDate, validDate.plusDays(1), validAddress);
        Set actual = destination.getNightsOfStay();
        Set expected = Sets.newHashSet(validDate.plusDays(1));
        assertEquals(expected, actual);
    }
}
