package gov.nysenate.ess.travel.allowance;

import gov.nysenate.ess.core.annotation.UnitTest;
import gov.nysenate.ess.travel.allowance.gsa.model.GsaAllowance;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@Category(UnitTest.class)
public class GsaAllowanceTest {

    @Test
    public void zeroValues_areValid() {
        new GsaAllowance(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
        assertTrue(true);
    }

    @Test (expected = NullPointerException.class)
    public void nullValues_areInvalid() {
        BigDecimal bd = null;
        new GsaAllowance(bd, bd, bd);
    }

    @Test (expected = NumberFormatException.class)
    public void emptyStrings_areInvalid() {
        new GsaAllowance("", "", "");
    }

    @Test (expected = NumberFormatException.class)
    public void nonNumberStrings_areInvalid() {
        new GsaAllowance("0A", ".", "x34");
    }

    @Test (expected = IllegalArgumentException.class)
    public void negativeValues_areInvalid() {
        new GsaAllowance("-0.000001", "0", "0");
    }

    @Test
    public void roundsToTwoDigits() {
        GsaAllowance actual = new GsaAllowance("0.005", "2.004", "3.14159265358");
        GsaAllowance expected = new GsaAllowance("0.01", "2.00", "3.14");
        assertEquals(expected, actual);
    }

    @Test
    public void testTotal() {
        GsaAllowance gsa = new GsaAllowance("0.3333333", "2.333333", "987654321");
        BigDecimal actual = gsa.total();
        BigDecimal expected = new BigDecimal("987654323.66");
        assertEquals(expected, actual);
    }
}
