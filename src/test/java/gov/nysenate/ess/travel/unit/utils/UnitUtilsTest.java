package gov.nysenate.ess.travel.unit.utils;

import gov.nysenate.ess.core.annotation.UnitTest;
import gov.nysenate.ess.travel.utils.UnitUtils;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;

@org.junit.experimental.categories.Category(UnitTest.class)
public class UnitUtilsTest {

    @Test
    public void testMetersToMiles() {
        long meters = 1609;
        BigDecimal expectedMiles = new BigDecimal("1.0");
        assertEquals(expectedMiles, UnitUtils.metersToMiles(meters));

        meters = 987654321;
        expectedMiles = new BigDecimal("613699.9");
        assertEquals(expectedMiles, UnitUtils.metersToMiles(meters));
    }
}
