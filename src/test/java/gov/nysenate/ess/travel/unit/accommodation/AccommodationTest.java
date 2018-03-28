package gov.nysenate.ess.travel.unit.accommodation;

import com.google.common.collect.ImmutableSet;
import gov.nysenate.ess.core.annotation.UnitTest;
import gov.nysenate.ess.core.model.unit.Address;
import gov.nysenate.ess.travel.accommodation.Accommodation;
import gov.nysenate.ess.travel.utils.Dollars;
import gov.nysenate.ess.travel.accommodation.Stay;
import gov.nysenate.ess.travel.fixtures.AccommodationFixture;
import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;

@UnitTest
public class AccommodationTest {

    @Test
    public void calculatesAllowances() {
        Accommodation a = AccommodationFixture.twoDayOneNightAccommodation();
        assertEquals(new Dollars("68.00"), a.mealAllowance());
        assertEquals(new Dollars("120.00"), a.lodgingAllowance());
    }

    @Test
    public void usersCanRequestNoAllowances() {
        ImmutableSet<Stay> stays = AccommodationFixture.twoDayOneNightStays();
        Accommodation a = new Accommodation(new Address(), stays, false, false);
        assertEquals(new Dollars("0"), a.mealAllowance());
        assertEquals(new Dollars("0"), a.lodgingAllowance());
    }

    @Test
    public void arrivalDate() {
        Accommodation a = AccommodationFixture.twoDayOneNightAccommodation();
        assertEquals(LocalDate.of(2018, 1, 1), a.arrivalDate());
    }

    @Test
    public void departureDate() {
        Accommodation a = AccommodationFixture.twoDayOneNightAccommodation();
        assertEquals(LocalDate.of(2018, 1, 2), a.departureDate());
    }
}
