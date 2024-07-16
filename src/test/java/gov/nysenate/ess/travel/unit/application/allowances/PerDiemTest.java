package gov.nysenate.ess.travel.unit.application.allowances;

import gov.nysenate.ess.core.annotation.UnitTest;
import gov.nysenate.ess.travel.request.allowances.PerDiem;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.Assert.assertEquals;

@Category(UnitTest.class)
public class PerDiemTest {

    @Test
    public void testCompareTo() {
        PerDiem one = new PerDiem(LocalDate.of(2019, 1, 5), new BigDecimal("1"));
        PerDiem two = new PerDiem(LocalDate.of(2019, 1, 5), new BigDecimal("1"));
        assertEquals(0, one.compareTo(two));

        two = new PerDiem(LocalDate.of(2019, 1, 4), new BigDecimal("1"));
        assertEquals(1, one.compareTo(two));

        two = new PerDiem(LocalDate.of(2019, 1, 5), new BigDecimal("5"));
        assertEquals(-1, one.compareTo(two));
    }
}
