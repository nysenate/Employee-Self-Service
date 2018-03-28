package gov.nysenate.ess.travel.unit;

import gov.nysenate.ess.core.annotation.UnitTest;
import gov.nysenate.ess.travel.utils.Dollars;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@UnitTest
public class DollarsTest {

    @Test (expected = NullPointerException.class)
    public void cantBeNull() {
        String dollars = null;
        Dollars d = new Dollars(dollars);
    }

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
}
