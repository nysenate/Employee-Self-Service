package gov.nysenate.ess.travel.unit.utils;

import gov.nysenate.ess.core.annotation.UnitTest;
import gov.nysenate.ess.travel.utils.Dollars;
import org.junit.Test;

import static org.junit.Assert.*;

@org.junit.experimental.categories.Category(UnitTest.class)
public class DollarsTest {

    @Test
    public void roundsToTwoDecimalPlaces() {
        Dollars d = new Dollars("4.12345");
        assertEquals(new Dollars("4.12"), d);
    }

    @Test
    public void usesHalfUpRounding() {
        Dollars d = new Dollars("4.4949");
        assertEquals(new Dollars("4.49"), d);

        d = new Dollars("4.4950");
        assertEquals(new Dollars("4.50"), d);
    }

    @Test
    public void adds() {
        Dollars expected = new Dollars("5.34");
        assertEquals(expected, new Dollars("4.21").add(new Dollars("1.13")));
    }

    @Test
    public void multiply() {
        Dollars expected = new Dollars("6.30");
        assertEquals(expected, new Dollars("2.10").multiply(new Dollars("3.00")));
    }

    @Test
    public void testComparable() {
        Dollars a =  new Dollars("1");
        Dollars b =  new Dollars("1");
        assertEquals(a.compareTo(b), 0);
        assertEquals(a, b);

        b = new Dollars("2");
        assertNotEquals(a.compareTo(b), 0);
        assertNotEquals(a, b);
    }
}
