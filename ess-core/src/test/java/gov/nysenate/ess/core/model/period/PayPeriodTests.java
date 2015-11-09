package gov.nysenate.ess.core.model.period;

import com.google.common.collect.BoundType;
import com.google.common.collect.Range;
import gov.nysenate.ess.core.annotation.ProperTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;

import static org.junit.Assert.*;

@Category(ProperTest.class)
public class PayPeriodTests
{
    private static final Logger logger = LoggerFactory.getLogger(PayPeriodTests.class);

    @Before
    public void setUp() throws Exception {}

    @After
    public void tearDown() throws Exception {}

    /** --- Functional Getter/Setter Tests --- */

    @Test
    public void testGetNumDaysInPeriod_simpleCases() throws Exception {
        PayPeriod p = new PayPeriod();
        p.setStartDate(LocalDate.of(2015, 11, 4));
        p.setEndDate(LocalDate.of(2015, 11, 11));
        assertEquals(8, p.getNumDaysInPeriod());

        // Test single date
        p.setStartDate(LocalDate.of(2015, 11, 4));
        p.setEndDate(LocalDate.of(2015, 11, 4));
        assertEquals(1, p.getNumDaysInPeriod());

        // Spans more than 1 year
        p.setStartDate(LocalDate.of(2015, 12, 31));
        p.setEndDate(LocalDate.of(2016, 1, 1));
        assertEquals(2, p.getNumDaysInPeriod());
    }

    /** Test num days in pay period when there is a leap year */
    @Test
    public void testGetNumDaysInPeriod_leapYear() throws Exception {
        PayPeriod p = new PayPeriod();
        p.setStartDate(LocalDate.of(2012, 1, 1));
        p.setEndDate(LocalDate.of(2012, 12, 31));
        assertEquals(366, p.getNumDaysInPeriod());
    }

    /** Test invalid date range for num days in pay period */
    @Test(expected = IllegalStateException.class)
    public void testGetNumDaysInPeriod_invalidDateRange() throws Exception {
        PayPeriod p = new PayPeriod();
        p.setStartDate(LocalDate.of(2015, 11, 4));
        p.setEndDate(LocalDate.of(2015, 11, 3));
        p.getNumDaysInPeriod();
    }

    /** Test invalid start end dates for num days in pay period */
    @Test(expected = IllegalStateException.class)
    public void testGetNumDaysInPeriod_nullDateRange() throws Exception {
        PayPeriod p = new PayPeriod();
        p.setStartDate(LocalDate.of(2015, 11, 4));
        p.setEndDate(null);
        p.getNumDaysInPeriod();
    }

    /** Test basic num week day cases */
    @Test
    public void testGetNumWeekDaysInPeriod_simpleCases() throws Exception {
        PayPeriod p = new PayPeriod();
        p.setStartDate(LocalDate.of(2015, 11, 4));
        p.setEndDate(LocalDate.of(2015, 11, 18));
        assertEquals(11, p.getNumWeekDaysInPeriod());

        p.setStartDate(LocalDate.of(2015, 11, 7)); //saturday
        p.setEndDate(LocalDate.of(2015, 11, 8)); //sunday
        assertEquals(0, p.getNumWeekDaysInPeriod());

        p.setStartDate(LocalDate.of(2015, 11, 9)); //monday
        p.setEndDate(LocalDate.of(2015, 11, 9)); //monday
        assertEquals(1, p.getNumWeekDaysInPeriod());
    }

    /** Test invalid date range for num week days in pay period */
    @Test(expected = IllegalStateException.class)
    public void testGetNumWeekDaysInPeriod_invalidDateRange() throws Exception {
        PayPeriod p = new PayPeriod();
        p.setStartDate(LocalDate.of(2015, 11, 4));
        p.setEndDate(LocalDate.of(2015, 11, 3));
        p.getNumWeekDaysInPeriod();
    }

    /** Test invalid start end dates for num week days in pay period */
    @Test(expected = IllegalStateException.class)
    public void testGetNumWeekDaysInPeriod_nullDateRange() throws Exception {
        PayPeriod p = new PayPeriod();
        p.setStartDate(LocalDate.of(2015, 11, 4));
        p.setEndDate(null);
        p.getNumWeekDaysInPeriod();
    }

    /** A pay period will typically have 14 days. */
    @Test
    public void testDefaultPayPeriodDays() throws Exception {
        PayPeriod p = new PayPeriod();
        assertEquals(14, p.DEFAULT_PAY_PERIOD_DAYS);
    }

    /** A pay period that ends cleanly on the year should not be an end of year split. */
    @Test
    public void testIsEndOfYearSplit_perfectYear() throws Exception {
        PayPeriod p = new PayPeriod();
        p.setStartDate(LocalDate.of(2014, 12, 18));
        p.setEndDate(LocalDate.of(2014, 12, 31));
        assertFalse(p.isEndOfYearSplit());
        assertFalse(p.isStartOfYearSplit());
    }

    /** A pay period that ends abruptly on the year should be an end of year split. */
    @Test
    public void testIsEndOfYearSplit_hasSplit() throws Exception {
        PayPeriod p = new PayPeriod();
        p.setStartDate(LocalDate.of(2014, 12, 19));
        p.setEndDate(LocalDate.of(2014, 12, 31));
        assertTrue(p.isEndOfYearSplit());
        assertFalse(p.isStartOfYearSplit());
    }

    /** A pay period that starts cleanly on the year should not be a start of year split. */
    @Test
    public void testIsStartOfYearSplit_perfectYear() throws Exception {
        PayPeriod p = new PayPeriod();
        p.setStartDate(LocalDate.of(2015, 1, 1));
        p.setEndDate(LocalDate.of(2015, 1, 14));
        assertFalse(p.isEndOfYearSplit());
        assertFalse(p.isStartOfYearSplit());
    }

    /** A pay period that starts abruptly on the year should be a start of year split. */
    @Test
    public void testIsStartOfYearSplit_hasSplit() throws Exception {
        PayPeriod p = new PayPeriod();
        p.setStartDate(LocalDate.of(2015, 1, 1));
        p.setEndDate(LocalDate.of(2015, 1, 10));
        assertFalse(p.isEndOfYearSplit());
        assertTrue(p.isStartOfYearSplit());
    }

    /** Get range should return a closed open range to make contiguous ranges easier. */
    @Test
    public void testGetDateRange_returnsClosedOpenRange() throws Exception {
        PayPeriod p = new PayPeriod();
        p.setStartDate(LocalDate.of(2015, 1, 1));
        p.setEndDate(LocalDate.of(2015, 1, 14));
        Range<LocalDate> dateRange = p.getDateRange();
        assertEquals(BoundType.CLOSED, dateRange.lowerBoundType());
        assertEquals(BoundType.OPEN, dateRange.upperBoundType());
    }

    /** Get range should return an upper bound date of one after the end date. */
    @Test
    public void testGetDateRange_returnsOnePlusEndDate() throws Exception {
        PayPeriod p = new PayPeriod();
        p.setStartDate(LocalDate.of(2015, 1, 1));
        p.setEndDate(LocalDate.of(2015, 1, 14));
        Range<LocalDate> dateRange = p.getDateRange();
        assertEquals(LocalDate.of(2015, 1, 1), dateRange.lowerEndpoint());
        assertEquals(LocalDate.of(2015, 1, 15), dateRange.upperEndpoint());
    }

    @Test
    public void testGetYear_returnsCorrectYear() throws Exception {
        PayPeriod p = new PayPeriod();
        p.setEndDate(LocalDate.of(2015, 1, 1));
        assertEquals(2015, p.getYear());
        // Overlapping year should still take end date
        p.setStartDate(LocalDate.of(2015, 12, 31));
        p.setEndDate(LocalDate.of(2016, 12, 31));
        assertEquals(2016, p.getYear());
    }

    /** --- Equals/Comparison tests --- */

    /** Two pay periods are equal if their type and dates are the same. */
    @Test
    public void testEquals_identicalDatesReturnsTrue() throws Exception {
        PayPeriod p1 = new PayPeriod();
        p1.setType(PayPeriodType.AF);
        p1.setStartDate(LocalDate.now());
        p1.setEndDate(LocalDate.now().plusDays(14));

        PayPeriod p2 = new PayPeriod();
        p2.setType(PayPeriodType.AF);
        p2.setStartDate(LocalDate.now());
        p2.setEndDate(LocalDate.now().plusDays(14));

        assertEquals(p1, p2);
    }

    /** Two pay periods are not equal if their type and/or dates are not the same. */
    @Test
    public void testEquals_mismatchDatesReturnsFalse() throws Exception {
        PayPeriod p1 = new PayPeriod();
        p1.setType(PayPeriodType.AF);
        p1.setStartDate(LocalDate.now());
        p1.setEndDate(LocalDate.now().plusDays(14));

        PayPeriod p2 = new PayPeriod();
        p2.setType(PayPeriodType.PA);
        p2.setStartDate(LocalDate.now());
        p2.setEndDate(LocalDate.now().plusDays(14));

        assertNotEquals(p1, p2);

        p2.setType(PayPeriodType.AF);
        assertEquals(p1, p2);

        p2.setEndDate(LocalDate.now().plusDays(13));
        assertNotEquals(p1, p2);
    }

    /** A pay period should be ordered based on it's start date. */
    @Test
    public void testCompareTo_basedOnDate() throws Exception {
        PayPeriod p1 = new PayPeriod();
        p1.setStartDate(LocalDate.now());
        p1.setEndDate(LocalDate.now().plusDays(14));

        PayPeriod p2 = new PayPeriod();
        p2.setStartDate(LocalDate.now().plusDays(3));
        p2.setEndDate(LocalDate.now().plusDays(14));

        assertEquals(-1, p1.compareTo(p2));
        p2.setStartDate(LocalDate.now());
        assertEquals(0, p1.compareTo(p2));
        p2.setStartDate(LocalDate.now().minusDays(1));
        assertEquals(1, p1.compareTo(p2));
    }
}