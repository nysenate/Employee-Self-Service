package gov.nysenate.ess.time.model.payroll;

import gov.nysenate.ess.core.annotation.UnitTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@Category(UnitTest.class)
public class DeductionTest {

    private Deduction nyTax = new Deduction("1", 1, "NY Tax", BigDecimal.ZERO);
    private Deduction nysTax = new Deduction("1", 1, "NYS Tax", BigDecimal.ZERO);
    private Deduction healthInsurance = new Deduction("2", 2, "Health Insurance", BigDecimal.ZERO);
    private Deduction fedTax = new Deduction("4", 1, "Fed Tax", BigDecimal.ZERO);

    @Test
    public void sortedByOrderThenCode() {
        assertTrue(nyTax.compareTo(healthInsurance) < 0);
        assertTrue(nyTax.compareTo(fedTax) < 0);
        assertTrue(fedTax.compareTo(nyTax) > 0);
        assertEquals(nyTax, nysTax);
    }

}
