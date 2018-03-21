package gov.nysenate.ess.travel.unit;

import gov.nysenate.ess.core.annotation.UnitTest;
import gov.nysenate.ess.travel.utils.Dollars;
import gov.nysenate.ess.travel.accommodation.NightStay;
import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;

@UnitTest
public class NightStayTest {

    @Test
    public void hasZeroMealAllowance() {
        NightStay nightStay = new NightStay(LocalDate.now(), new Dollars("50"));
        assertEquals(new Dollars("0"), nightStay.mealAllowance());
    }

    @Test
    public void hasLodgingAllowance() {
        NightStay nightStay = new NightStay(LocalDate.now(), new Dollars("50"));
        assertEquals(new Dollars("50.00"), nightStay.lodgingAllowance());
    }

}
