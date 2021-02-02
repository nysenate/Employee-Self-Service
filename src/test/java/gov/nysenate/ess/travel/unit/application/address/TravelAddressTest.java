package gov.nysenate.ess.travel.unit.application.address;

import gov.nysenate.ess.core.annotation.UnitTest;
import gov.nysenate.ess.travel.application.address.TravelAddress;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertEquals;

@Category(UnitTest.class)
public class TravelAddressTest {

    @Test
    public void givenNoAddressFields_countyFormatReturnsName() {
        String expected = "5 State Street";
        TravelAddress address = new TravelAddress.Builder()
                .withName("5 State Street")
                .build();
        assertEquals(expected, address.getFormattedAddressWithCounty());

        expected = expected + " (Albany County)";
        address = new TravelAddress.Builder()
                .withName("5 State Street")
                .withCounty("Albany")
                .build();
        assertEquals(expected, address.getFormattedAddressWithCounty());
    }

    @Test
    public void countyFormatUsesAddr1IfNoName() {
        String expected = "5 State Street, Albany, New York 12222 (Albany County)";
        TravelAddress address = new TravelAddress.Builder()
                .withAddr1("5 State Street")
                .withCity("Albany")
                .withZip5("12222")
                .withState("New York")
                .withCounty("Albany")
                .build();

        assertEquals(expected, address.getFormattedAddressWithCounty());
    }

    @Test
    public void countyFormatPrefersNameOverAddr1() {
        String expected = "Five State Street, Albany, New York 12222 (Albany County)";
        TravelAddress address = new TravelAddress.Builder()
                .withName("Five State Street")
                .withAddr1("5 State Street")
                .withCity("Albany")
                .withZip5("12222")
                .withState("New York")
                .withCounty("Albany")
                .build();

        assertEquals(expected, address.getFormattedAddressWithCounty());
    }

    @Test
    public void summaryFormatPriorityTest() {
        // Returns the name if it exists, ignoring other fields.
        TravelAddress address = new TravelAddress.Builder()
                .withName("Five State Street")
                .withAddr1("5 State Street")
                .withCity("Albany")
                .build();
        assertEquals("Five State Street", address.getSummary());

        // If no name, return addr1.
        address = new TravelAddress.Builder()
                .withAddr1("5 State Street")
                .withCity("Albany")
                .build();
        assertEquals("5 State Street", address.getSummary());

        // If no name or addr1, return city.
        address = new TravelAddress.Builder()
                .withCity("Albany")
                .build();
        assertEquals("Albany", address.getSummary());

        // If no name, addr1, and city. return empty string.
        address = new TravelAddress.Builder()
                .withZip5("12222")
                .withState("New York")
                .withCounty("Albany")
                .build();
        assertEquals("", address.getSummary());
    }
}
