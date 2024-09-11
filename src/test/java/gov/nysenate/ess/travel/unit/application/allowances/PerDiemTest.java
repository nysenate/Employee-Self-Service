package gov.nysenate.ess.travel.unit.application.allowances;

import gov.nysenate.ess.core.annotation.UnitTest;
import gov.nysenate.ess.travel.request.allowances.PerDiem;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@Category(UnitTest.class)
public class PerDiemTest {

    @Test
    public void testCompareTo() {
        PerDiem pd = new PerDiem(LocalDate.of(2019, 1, 5), new BigDecimal("1"));
        PerDiem equivalentPd = new PerDiem(LocalDate.of(2019, 1, 5), new BigDecimal("1"));
        assertEquals("PerDiem's should be equal.", 0, pd.compareTo(equivalentPd));

        PerDiem earlierPd = new PerDiem(LocalDate.of(2019, 1, 4), new BigDecimal("1"));
        assertEquals("PerDiem for 2019/01/05 should be greater than a PerDiem for 2019/01/04", 1, pd.compareTo(earlierPd));

        PerDiem laterPd = new PerDiem(LocalDate.of(2019, 1, 6), new BigDecimal("1"));
        assertEquals("PerDiem for 2019/01/05 should be less than a PerDiem for 2019/01/06", -1, pd.compareTo(laterPd));

        PerDiem one = new PerDiem(LocalDate.of(2019, 1, 5), new BigDecimal("1"));
        PerDiem five = new PerDiem(LocalDate.of(2019, 1, 5), new BigDecimal("5"));
        assertEquals("If days equal, higher rate PerDiem should be greater", -1, one.compareTo(five));
    }

    @Test
    public void isRateZeroIgnoresScale() {
        PerDiem pd = new PerDiem(LocalDate.of(2019, 1, 5), new BigDecimal("0"));
        assertTrue(pd.isRateZero());

        pd = new PerDiem(LocalDate.of(2019, 1, 5), new BigDecimal("00.000"));
        assertTrue(pd.isRateZero());
    }
}
