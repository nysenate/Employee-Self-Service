package gov.nysenate.ess.travel.unit.accommodation;

import gov.nysenate.ess.core.annotation.UnitTest;
import gov.nysenate.ess.travel.accommodation.Day;
import gov.nysenate.ess.travel.meal.MealTier;
import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

@UnitTest
public class DayTest {

    private static final LocalDate MONDAY = LocalDate.of(2018, 6, 4);
    private static final LocalDate TUESDAY = LocalDate.of(2018, 6, 5);

    private static final MealTier tier1 = new MealTier("50", "10", "15", "20", "5");
    private static final MealTier tier2 = new MealTier("100", "20", "30", "40", "10");

    @Test
    public void testComparable() {
        Day a = new Day(MONDAY, tier1, true);
        Day b = new Day(MONDAY, tier1, true);

        assertEquals(a.compareTo(b), 0);
        assertEquals(a, b);

        b = new Day(TUESDAY, tier1, true);
        assertNotEquals(a.compareTo(b), 0);
        assertNotEquals(a, b);

        b = new Day(MONDAY, tier2, true);
        assertNotEquals(a.compareTo(b), 0);
        assertNotEquals(a, b);

        b = new Day(MONDAY, tier1, false);
        assertNotEquals(a.compareTo(b), 0);
        assertNotEquals(a, b);
    }
}
