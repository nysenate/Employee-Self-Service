package gov.nysenate.ess.time.model.accrual;

import gov.nysenate.ess.core.annotation.UnitTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;

@Category(UnitTest.class)
public class AccrualRateTest
{    private static final Logger logger = LoggerFactory.getLogger(AccrualRateTest.class);

    @Test
    public void testVacationRatesAreCorrect() {
        assertEquals("0", AccrualRate.VACATION.getRate(3).toString());
        assertEquals("31.5", AccrualRate.VACATION.getRate(13).toString());
        assertEquals("3.5", AccrualRate.VACATION.getRate(14).toString());
        assertEquals("3.75", AccrualRate.VACATION.getRate(28).toString());
        assertEquals("4", AccrualRate.VACATION.getRate(53).toString());
        assertEquals("4", AccrualRate.VACATION.getRate(58).toString());
        assertEquals("5.5", AccrualRate.VACATION.getRate(89).toString());
    }

    @Test
    public void testSickRatesAreCorrect() {
        assertEquals("3.5", AccrualRate.SICK.getRate(3).toString());
        assertEquals("3.5", AccrualRate.SICK.getRate(13).toString());
        assertEquals("3.5", AccrualRate.SICK.getRate(900).toString());
    }
}
