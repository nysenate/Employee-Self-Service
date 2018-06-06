package gov.nysenate.ess.travel.unit.accommodation;

import gov.nysenate.ess.core.annotation.UnitTest;
import gov.nysenate.ess.travel.accommodation.Night;
import gov.nysenate.ess.travel.utils.Dollars;
import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

@UnitTest
public class NightTest {

    private static final LocalDate MONDAY = LocalDate.of(2018, 6, 4);
    private static final LocalDate TUESDAY = LocalDate.of(2018, 6, 5);

    @Test
    public void testComparable() {
        Night a = new Night(MONDAY, new Dollars("1"), true);
        Night b = new Night(MONDAY, new Dollars("1"), true);
        assertEquals(a.compareTo(b), 0);
        assertEquals(a, b);

        b = new Night(TUESDAY, new Dollars("1"), true);
        assertNotEquals(a.compareTo(b), 0);
        assertNotEquals(a, b);


        b = new Night(MONDAY, new Dollars("99"), true);
        assertNotEquals(a.compareTo(b), 0);
        assertNotEquals(a, b);


        b = new Night(MONDAY, new Dollars("1"), false);
        assertNotEquals(a.compareTo(b), 0);
        assertNotEquals(a, b);
    }
}
