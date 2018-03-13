package gov.nysenate.ess.travel.unit;

import gov.nysenate.ess.core.annotation.UnitTest;
import gov.nysenate.ess.travel.Dollars;
import gov.nysenate.ess.travel.meal.MealTier;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

@UnitTest
public class MealTierTest {

    @Test
    public void senateRateIsBreakfastAndDinner() {
        MealTier tier = new MealTier("50", "10", "15", "20", "5");
        assertEquals(new Dollars("30.00"), tier.allowance());
    }
}
