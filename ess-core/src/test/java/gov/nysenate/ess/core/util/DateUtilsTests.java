package gov.nysenate.ess.core.util;

import com.google.common.collect.Range;
import gov.nysenate.ess.core.annotation.ProperTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.*;

@Category(ProperTest.class)
public class DateUtilsTests
{
    @Test
    public void testFirstDayOfPreviousYear() throws Exception {
        assertEquals(LocalDate.of(2014, 1, 1), DateUtils.firstDayOfPreviousYear(LocalDate.of(2015, 9, 3)));
        assertEquals(LocalDate.of(2015, 1, 1), DateUtils.firstDayOfPreviousYear(LocalDate.of(2016, 1, 1)));
    }

    @Test
    public void testAtEndOfDay() throws Exception {
        LocalDate now = LocalDate.now();
        assertEquals(now.atTime(23, 59, 59, 999999999), DateUtils.atEndOfDay(now));
    }

    /** Long ago should be a date with year 1 AD. */
    @Test
    public void testLongAgo() throws Exception {
        assertEquals(1, DateUtils.longAgo().getYear());
    }

    /** Closed start bound should yield that date. Open start bound should return the day after. */
    @Test
    public void testStartOfDateRange() throws Exception {
        Range<LocalDate> closedDateRange = Range.closed(LocalDate.of(2015, 1, 1), LocalDate.now());
        assertEquals(LocalDate.of(2015, 1, 1), DateUtils.startOfDateRange(closedDateRange));
        Range<LocalDate> openDateRange = Range.open(LocalDate.of(2015, 1, 1), LocalDate.now());
        assertEquals(LocalDate.of(2015, 1, 2), DateUtils.startOfDateRange(openDateRange));
        // Test leap year too
        openDateRange = Range.open(LocalDate.of(2012, 2, 28), LocalDate.now());
        assertEquals(LocalDate.of(2012, 2, 29), DateUtils.startOfDateRange(openDateRange));
        openDateRange = Range.open(LocalDate.of(2013, 2, 28), LocalDate.now());
        assertEquals(LocalDate.of(2013, 3, 1), DateUtils.startOfDateRange(openDateRange));
        // End of year
        openDateRange = Range.open(LocalDate.of(2015, 12, 31), LocalDate.of(2016, 12, 31));
        assertEquals(LocalDate.of(2016, 1, 1), DateUtils.startOfDateRange(openDateRange));
        // Invalid range
        openDateRange = Range.openClosed(LocalDate.of(2015, 12, 31), LocalDate.of(2015, 12, 31));
        assertEquals(LocalDate.of(2016, 1, 1), DateUtils.startOfDateRange(openDateRange));
    }

    @Test
    public void testEndOfDateRange() throws Exception {
        Range<LocalDate> closedDateRange = Range.closed(LocalDate.of(2015, 1, 1), LocalDate.of(2015, 4, 1));
        assertEquals(LocalDate.of(2015, 4, 1), DateUtils.endOfDateRange(closedDateRange));
        Range<LocalDate> openDateRange = Range.open(LocalDate.of(2015, 1, 1), LocalDate.of(2015, 4, 1));
        assertEquals(LocalDate.of(2015, 3, 31), DateUtils.endOfDateRange(openDateRange));
        // Test leap year too
        openDateRange = Range.open(LocalDate.of(2012, 2, 27), LocalDate.of(2012, 2, 29));
        assertEquals(LocalDate.of(2012, 2, 28), DateUtils.endOfDateRange(openDateRange));
        openDateRange = Range.open(LocalDate.of(2012, 2, 28), LocalDate.of(2012, 3, 1));
        assertEquals(LocalDate.of(2012, 2, 29), DateUtils.endOfDateRange(openDateRange));
        // End of year
        openDateRange = Range.open(LocalDate.of(2015, 12, 31), LocalDate.of(2016, 1, 1));
        assertEquals(LocalDate.of(2015, 12, 31), DateUtils.endOfDateRange(openDateRange));
    }

    /** Start of open date range should be one nano second after start bound. */
    @Test
    public void testStartOfDateTimeRange() throws Exception {
        Range<LocalDateTime> closedDateRange = Range.closed(LocalDateTime.of(2015, 1, 1, 13, 14, 15, 123456789),
                LocalDateTime.of(2016, 1, 1, 13, 14, 15, 123456789));
        assertEquals(LocalDateTime.of(2015, 1, 1, 13, 14, 15, 123456789),
                                      DateUtils.startOfDateTimeRange(closedDateRange));
        // One more nano
        Range<LocalDateTime> openDateRange = Range.open(LocalDateTime.of(2015, 1, 1, 13, 14, 15, 123456789),
                LocalDateTime.of(2016, 1, 1, 13, 14, 15, 123456789));
        assertEquals(LocalDateTime.of(2015, 1, 1, 13, 14, 15, 123456790), DateUtils.startOfDateTimeRange(openDateRange));
        // Test leap year too
        openDateRange = Range.open(LocalDateTime.of(2012, 2, 28, 23, 59, 59, 999999999), LocalDateTime.now());
        assertEquals(LocalDateTime.of(2012, 2, 29,0,0,0,0), DateUtils.startOfDateTimeRange(openDateRange));
        // End of year
        openDateRange = Range.open(LocalDateTime.of(2015, 12, 31, 23, 59, 59, 999999999), LocalDateTime.of(2016, 12, 31, 0, 0, 0, 0));
        assertEquals(LocalDateTime.of(2016, 1, 1, 0,0,0), DateUtils.startOfDateTimeRange(openDateRange));
    }

    /** End of open date range shound be one nano second before end bound. */
    @Test
    public void testEndOfDateTimeRange() throws Exception {
        Range<LocalDateTime> closedDateRange = Range.closed(LocalDateTime.of(2015, 1, 1, 13, 14, 15, 123456789),
                LocalDateTime.of(2016, 1, 1, 13, 14, 15, 123456789));
        assertEquals(LocalDateTime.of(2016, 1, 1, 13, 14, 15, 123456789),
                DateUtils.endOfDateTimeRange(closedDateRange));
        // One less nano
        Range<LocalDateTime> openDateRange = Range.open(LocalDateTime.of(2015, 1, 1, 13, 14, 15, 123456789),
                LocalDateTime.of(2016, 1, 1, 13, 14, 15, 123456789));
        assertEquals(LocalDateTime.of(2016, 1, 1, 13, 14, 15, 123456788), DateUtils.endOfDateTimeRange(openDateRange));
        // Test leap year too
        openDateRange = Range.open(LocalDateTime.of(2012, 2, 1,0,0,0), LocalDateTime.of(2012, 3, 1, 0,0,0));
        assertEquals(LocalDateTime.of(2012, 2, 29, 23, 59, 59, 999999999), DateUtils.endOfDateTimeRange(openDateRange));
        // End of year
        openDateRange = Range.open(LocalDateTime.of(2015, 1, 1, 0,0,0), LocalDateTime.of(2016, 1, 1, 0,0,0));
        assertEquals(LocalDateTime.of(2015, 12, 31, 23, 59, 59, 999999999), DateUtils.endOfDateTimeRange(openDateRange));
    }

    @Test
    public void testToDate() throws Exception {
        LocalDate localDate = LocalDate.of(2015, 1, 1);
        Calendar instance = Calendar.getInstance();
        instance.clear();
        instance.set(2015, 0, 1, 0, 0, 0);
        Date date = instance.getTime();
        assertEquals(date, DateUtils.toDate(localDate));
    }

    /** Conversion from LocalDateTime to Date should be accurate up to the millisecond. */
    @Test
    public void testToDateTime() throws Exception {
        LocalDateTime localDate = LocalDateTime.of(2015, 1, 1, 13, 14, 15, 123000000);
        Calendar instance = Calendar.getInstance();
        instance.clear();
        instance.set(2015, 0, 1, 13, 14, 15);
        instance.set(Calendar.MILLISECOND, 123);
        Date date = instance.getTime();
        assertEquals(date, DateUtils.toDate(localDate));

        // Anything after the millsecond is ignored
        localDate = LocalDateTime.of(2015, 1, 1, 13, 14, 15, 123800000);
        assertEquals(date, DateUtils.toDate(localDate));
    }

    /** Get LocalDateTime should be accurate up to the millisecond. */
    @Test
    public void testGetLocalDateTime() throws Exception {
        Calendar instance = Calendar.getInstance();
        instance.clear();
        instance.set(2015, 0, 1, 13, 14, 15);
        instance.set(Calendar.MILLISECOND,123);

        assertEquals(LocalDateTime.of(2015, 1, 1, 13, 14, 15, 123000000),
            DateUtils.getLocalDateTime(instance.getTime()));
        assertNotEquals(LocalDateTime.of(2015, 1, 1, 13, 14, 15, 123400000),
                DateUtils.getLocalDateTime(instance.getTime()));
    }

    @Test
    public void testGetLocalDate() throws Exception {
        Calendar instance = Calendar.getInstance();
        instance.clear();
        instance.set(2015, 0, 1, 13, 14, 15);
        instance.set(Calendar.MILLISECOND, 123);

        assertEquals(LocalDate.of(2015, 1, 1), DateUtils.getLocalDate(instance.getTime()));

        // Check leap years too
        instance.clear();
        instance.set(2012, 1, 29);
        assertEquals(LocalDate.of(2012, 2, 29), DateUtils.getLocalDate(instance.getTime()));
    }
}