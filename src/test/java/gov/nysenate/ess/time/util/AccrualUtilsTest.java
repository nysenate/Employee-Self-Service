package gov.nysenate.ess.time.util;

import gov.nysenate.ess.core.annotation.UnitTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.math.BigDecimal;

import static gov.nysenate.ess.time.model.EssTimeConstants.MAX_YTD_HOURS;
import static gov.nysenate.ess.time.model.EssTimeConstants.SICK_VAC_INCREMENT;
import static gov.nysenate.ess.time.util.AccrualUtils.getProratePercentage;
import static gov.nysenate.ess.time.util.AccrualUtils.roundSickVacHours;
import static org.junit.Assert.assertEquals;

@Category(UnitTest.class)
public class AccrualUtilsTest {
    @Test
    public void testGetProratePercentage() {
        BigDecimal halfHours = MAX_YTD_HOURS.divide(new BigDecimal(2));
        BigDecimal quarterHours = MAX_YTD_HOURS.divide(new BigDecimal(4));

        assertEquals(new BigDecimal("0.5"), getProratePercentage(halfHours));
        assertEquals(new BigDecimal("0.25"), getProratePercentage(quarterHours));
    }

    @Test
    public void testRoundAccrualValue() {
        BigDecimal roundedValue = SICK_VAC_INCREMENT.multiply(new BigDecimal(26));
        // Must use compareTo() due to potential scale differences
        assertEquals(0, roundedValue.compareTo(roundSickVacHours(roundedValue)));
        BigDecimal lessThanAccInc = SICK_VAC_INCREMENT.divide(BigDecimal.TEN);
        BigDecimal unRoundedValue = roundedValue.add(lessThanAccInc);
        BigDecimal expectedRound = new BigDecimal("6.75");
        assertEquals(0, expectedRound.compareTo(roundSickVacHours(unRoundedValue)));
    }
}
