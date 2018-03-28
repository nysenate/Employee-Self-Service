package gov.nysenate.ess.travel.unit.accommodation;

import gov.nysenate.ess.core.annotation.UnitTest;
import gov.nysenate.ess.travel.accommodation.DayStay;
import gov.nysenate.ess.travel.utils.Dollars;
import gov.nysenate.ess.travel.meal.MealTier;
import gov.nysenate.ess.travel.fixtures.MealRatesFixture;
import org.junit.Test;

import java.time.LocalDate;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

@UnitTest
public class DayStayTest {

    private static MealTier TIER_51 = MealRatesFixture.mealRatesFor2018().getTier("51");

    @Test
    public void hasZeroLodgingAllowance() {
        DayStay dayStay = new DayStay(LocalDate.now(), TIER_51);
        assertEquals(new Dollars("0"), dayStay.lodgingAllowance());
    }

    @Test
    public void hasMealAllowance() {
        DayStay dayStay = new DayStay(LocalDate.now(), TIER_51);
        assertEquals(new Dollars("34.00"), dayStay.mealAllowance());
    }

}
