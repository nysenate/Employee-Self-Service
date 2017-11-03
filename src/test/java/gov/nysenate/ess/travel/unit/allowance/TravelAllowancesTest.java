package gov.nysenate.ess.travel.unit.allowance;

import gov.nysenate.ess.core.annotation.UnitTest;
import gov.nysenate.ess.travel.allowance.gsa.model.GsaAllowance;
import gov.nysenate.ess.travel.application.model.TravelAllowances;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertEquals;

@Category(UnitTest.class)
public class TravelAllowancesTest {

    private static final GsaAllowance GSA = new GsaAllowance("0", "0", "0");

    @Test (expected = NullPointerException.class)
    public void nullValues_areInvalid() {
        new TravelAllowances(null, "0", "0", "0", "0", "0");
    }

    @Test (expected = NumberFormatException.class)
    public void emptyStrings_areInvalid() {
        new TravelAllowances(GSA, "", "", "", "", "");
    }

    @Test (expected = NumberFormatException.class)
    public void nonNumberStrings_areInvalid() {
        new TravelAllowances(GSA, "A", "445z", "x1", "_", "}");
    }

    @Test (expected = IllegalArgumentException.class)
    public void negativeValues_areInvalid() {
        new TravelAllowances(GSA, "-0.0000001", "-12.00", "0", "0", "0");
    }

    @Test
    public void roundToTwoDigits() {
        TravelAllowances actual = new TravelAllowances(GSA, "0.004", "0.005", "0.0000001", "0", "3.14159265358");
        TravelAllowances expected = new TravelAllowances(GSA, "0.00", "0.01", "0.00", "0", "3.14");
        assertEquals(expected, actual);
    }

    @Test
    public void totalSumsAllAllowances() {
        TravelAllowances allowances = new TravelAllowances(new GsaAllowance("2.25", "0", "0"),"1.00", "3.50", "7.25", "0", "0");
        assertEquals("14.00", allowances.total().toString());
    }
}
