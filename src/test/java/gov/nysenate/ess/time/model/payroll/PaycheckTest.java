package gov.nysenate.ess.time.model.payroll;

import gov.nysenate.ess.core.annotation.UnitTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.Assert.assertTrue;

@Category(UnitTest.class)
public class PaycheckTest {

    private Paycheck one = new Paycheck("1", LocalDate.of(2021, 1, 1),
            BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
    private Paycheck two = new Paycheck("1", LocalDate.of(2021, 1, 7),
            BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);

    @Test
    public void sortedByCheckDate() {
        assertTrue(one.compareTo(one) == 0);
        assertTrue(one.compareTo(two) < 0);
        assertTrue(two.compareTo(one) > 0);
    }
}
