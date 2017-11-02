package gov.nysenate.ess.travel.application;

import gov.nysenate.ess.core.annotation.UnitTest;
import gov.nysenate.ess.core.model.unit.Address;
import gov.nysenate.ess.travel.application.model.TravelDestination;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.time.LocalDate;

@Category(UnitTest.class)
public class TravelDestinationTest {

    private LocalDate validDate = LocalDate.now();
    private Address validAddress = new Address("101 Washington Ave", "Albany", "NY", "12210");

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
}
