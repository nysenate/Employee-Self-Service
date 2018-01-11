package gov.nysenate.ess.web;

import gov.nysenate.ess.core.annotation.UnitTest;
import gov.nysenate.ess.travel.utils.UnitUtils;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;

@Category(UnitTest.class)
public class UnitUtilsTest {

    @Test
    public void testMetersToMiles() {
        assertEquals(new BigDecimal("0.0"), UnitUtils.metersToMiles(0L));
        assertEquals(new BigDecimal("6.2"), UnitUtils.metersToMiles(10000L));
        assertEquals(new BigDecimal("35.0"), UnitUtils.metersToMiles(56327L));
        assertEquals(new BigDecimal("336.3"), UnitUtils.metersToMiles(541213L));
    }
}
