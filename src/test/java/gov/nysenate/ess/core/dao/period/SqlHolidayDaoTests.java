package gov.nysenate.ess.core.dao.period;

import com.google.common.collect.Range;
import gov.nysenate.ess.core.DaoTests;
import gov.nysenate.ess.core.annotation.ProperTest;
import gov.nysenate.ess.core.annotation.TestDependsOnDatabase;
import gov.nysenate.ess.core.model.period.Holiday;
import gov.nysenate.ess.core.model.period.HolidayNotFoundForDateEx;
import gov.nysenate.ess.core.util.DateUtils;
import gov.nysenate.ess.core.util.SortOrder;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;

import static java.time.LocalDate.now;
import static java.time.LocalDate.of;
import static org.junit.Assert.*;

@Category({ProperTest.class, TestDependsOnDatabase.class})
public class SqlHolidayDaoTests extends DaoTests
{
    private static final Logger logger = LoggerFactory.getLogger(SqlHolidayDaoTests.class);

    @Autowired protected SqlHolidayDao holidayDao;

    /** Holiday not found exception should be thrown if no holiday matches the given date. */
    @Test(expected = HolidayNotFoundForDateEx.class)
    public void testGetHoliday_noHoliday() throws Exception {
        holidayDao.getHoliday(of(2014, 4, 4));
    }

    @Test
    public void testGetHoliday_official() throws Exception {
        // CHRISTMAS DAY
        Holiday holiday = holidayDao.getHoliday(of(2014, 12, 25));
        assertNotNull(holiday);
        assertEquals(of(2014, 12, 25), holiday.getDate());
        assertEquals(new BigDecimal("7"), holiday.getHours());
        assertEquals("CHRISTMAS DAY", holiday.getName());
        assertFalse(holiday.isQuestionable());
        assertTrue(holiday.isActive());
    }

    @Test
    public void testGetHolidays_none() throws Exception {
        assertTrue(holidayDao.getHolidays(
                Range.closed(of(2040, 1, 1), of(2041, 1, 1)), false, SortOrder.ASC).isEmpty());
    }

    @Test
    public void testGetHolidays_2014() throws Exception {
        List<Holiday> holidays = holidayDao.getHolidays(Range.closed(of(2014, 1, 1), of(2014, 12, 31)), true, SortOrder.ASC);
        assertFalse(holidays.isEmpty());
        // Includes new years
        assertEquals(of(2014, 1, 1), holidays.get(0).getDate());
        // Sort order correct
        for (int i = holidays.size() - 1; i >= 1; i--) {
            holidays.get(i).getDate().isAfter(holidays.get(i - 1).getDate());
        }
        holidays = holidayDao.getHolidays(Range.closed(of(2014, 1, 1), of(2014, 12, 31)), true, SortOrder.DESC);
        for (int i = holidays.size() - 1; i >= 1; i--) {
            holidays.get(i).getDate().isBefore(holidays.get(i - 1).getDate());
        }
    }

    @Test
    public void testGetHolidays_ranges() throws Exception {
        // between new years and christmas
        List<Holiday> closedRangeHolidays = holidayDao.getHolidays(Range.closed(of(2014, 1, 1), of(2014, 12, 25)), false, SortOrder.ASC);
        List<Holiday> openRangeHolidays = holidayDao.getHolidays(Range.open(of(2014, 1, 1), of(2014, 12, 25)), false, SortOrder.ASC);
        // the open range should have two holidays less than the closed range
        assertEquals(closedRangeHolidays.size() - 2, openRangeHolidays.size());
    }

    @Test
    public void testGetHolidays_noQuestionable() throws Exception {
        List<Holiday> holidays = holidayDao.getHolidays(Range.closed(of(2011, 1, 1), now()), false, SortOrder.ASC);
        assertTrue(holidays.stream().noneMatch(Holiday::isQuestionable));
    }

    @Test
    public void testGetHolidays_someQuestionable() throws Exception {
        List<Holiday> holidays = holidayDao.getHolidays(Range.open(of(2011, 1, 1), DateUtils.THE_FUTURE), true, SortOrder.ASC);
        assertTrue(holidays.stream().anyMatch(Holiday::isQuestionable));
    }
}