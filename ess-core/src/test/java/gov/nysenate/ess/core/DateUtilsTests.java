package gov.nysenate.ess.core;

import gov.nysenate.ess.core.util.DateUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;

public class DateUtilsTests
{
    private static final Logger logger = LoggerFactory.getLogger(DateUtilsTests.class);

    @Test
    public void testDateRangeIntersectsTest() throws Exception {

    }

    @Test
    public void testFirstDayOfPreviousYear() throws Exception {
        assertEquals(LocalDate.of(2014, 1, 1), DateUtils.firstDayOfPreviousYear(LocalDate.of(2015, 9, 3)));
        assertEquals(LocalDate.of(2015, 1, 1), DateUtils.firstDayOfPreviousYear(LocalDate.of(2016, 1, 1)));
    }
}