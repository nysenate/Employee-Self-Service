package gov.nysenate.ess.travel.allowance;

import gov.nysenate.ess.core.annotation.UnitTest;
import gov.nysenate.ess.travel.allowance.gsa.model.GsaAllowance;
import gov.nysenate.ess.travel.application.model.TravelAppAllowances;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertEquals;

@Category(UnitTest.class)
public class TravelAppAllowancesTest {

    @Test
    public void allZerosSumToZero() {
        TravelAppAllowances allowances = new TravelAppAllowances(new GsaAllowance("0", "0", "0"),"0", "0", "0", "0", "0");
        assertEquals("0.00", allowances.total().toString());
    }

    @Test
    public void singleAllowanceSumsToThatAllowance() {
        TravelAppAllowances allowances = new TravelAppAllowances(new GsaAllowance("0", "0", "0"),"1.39", "0", "0", "0", "0");
        assertEquals("1.39", allowances.total().toString());
    }

    @Test
    public void sumsMultipleAllowances() {
        TravelAppAllowances allowances = new TravelAppAllowances(new GsaAllowance("2.25", "0", "0"),"1.00", "3.50", "7.25", "0", "0");
        assertEquals("14.00", allowances.total().toString());
    }

    @Test
    public void allowancesRoundedToTwoDigits() {
        TravelAppAllowances allowances = new TravelAppAllowances(new GsaAllowance("0", "0", "0"),"1.334", "2.335", "0", "0", "0");
        assertEquals("3.67", allowances.total().toString());
    }
}
