package gov.nysenate.ess.time.util;

import gov.nysenate.ess.core.annotation.UnitTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.math.BigDecimal;

import static gov.nysenate.ess.time.model.EssTimeConstants.SICK_VAC_INCREMENT;
import static gov.nysenate.ess.time.model.EssTimeConstants.MAX_YTD_HOURS;
import static org.junit.Assert.*;
import static gov.nysenate.ess.time.util.AccrualUtils.*;

@Category(UnitTest.class)
public class AccrualUtilsTest {

    @Test
    public void testGetProratePercentage() throws Exception {
        BigDecimal halfHours = MAX_YTD_HOURS.divide(new BigDecimal(2));
        BigDecimal quarterHours = MAX_YTD_HOURS.divide(new BigDecimal(4));

        assertTrue(new BigDecimal(0.5).compareTo(getProratePercentage(halfHours)) == 0);
        assertTrue(new BigDecimal(0.25).compareTo(getProratePercentage(quarterHours)) == 0);
    }

    @Test
    public void testRoundAccrualValue() throws Exception {
        BigDecimal roundedValue = SICK_VAC_INCREMENT.multiply(new BigDecimal(26));

        assertTrue(roundedValue.compareTo(roundSickVacHours(roundedValue)) == 0);

        BigDecimal lessThanAccInc = SICK_VAC_INCREMENT.divide(BigDecimal.TEN);

        BigDecimal unRoundedValue = roundedValue.add(lessThanAccInc);

        BigDecimal expectedRound = new BigDecimal(6.5);

        assertTrue(expectedRound.compareTo(roundSickVacHours(unRoundedValue)) == 0);
    }

}