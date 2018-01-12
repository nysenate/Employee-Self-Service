package gov.nysenate.ess.travel.unit.utils;

import gov.nysenate.ess.core.annotation.UnitTest;
import gov.nysenate.ess.travel.utils.UnitUtils;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;

@Category(UnitTest.class)
public class UnitUtilsTest {

    @Test(expected = IllegalArgumentException.class)
    public void negativeMetersThrowsException() {
        UnitUtils.metersToMiles(-1000L);
    }

    @Test
    public void testMetersToMiles() {
        assertEquals(new BigDecimal("0.0"), UnitUtils.metersToMiles(0L));
        assertEquals(new BigDecimal("6.2"), UnitUtils.metersToMiles(10000L));
        assertEquals(new BigDecimal("35.0"), UnitUtils.metersToMiles(56327L));
        assertEquals(new BigDecimal("336.3"), UnitUtils.metersToMiles(541213L));
    }

    @Test
    public void testRoundToHundredth() {
        assertEquals(new BigDecimal("0.01"), UnitUtils.roundToHundredth(new BigDecimal("0.014999")));
        assertEquals(new BigDecimal("-0.02"), UnitUtils.roundToHundredth(new BigDecimal("-0.015000001")));
        assertEquals(new BigDecimal("7654762.41"), UnitUtils.roundToHundredth(new BigDecimal("7654762.40891727")));
    }

    @Test
    public void testRoundToTenth() {
        assertEquals(new BigDecimal("0.1"), UnitUtils.roundToTenth(new BigDecimal("0.14999")));
        assertEquals(new BigDecimal("-0.2"), UnitUtils.roundToTenth(new BigDecimal("-0.15000001")));
        assertEquals(new BigDecimal("7654762.4"), UnitUtils.roundToTenth(new BigDecimal("7654762.40891727")));
    }
}
