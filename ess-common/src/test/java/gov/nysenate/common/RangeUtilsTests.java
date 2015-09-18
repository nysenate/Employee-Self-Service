package gov.nysenate.common;

import com.google.common.collect.RangeMap;
import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Logger;

@ProperTest
public class RangeUtilsTests
{
    private Logger logger = Logger.getAnonymousLogger();

    @Test
    public void testToRangeMap() throws Exception {
        TreeMap<LocalDate, Boolean> evenNumberOfDates = new TreeMap<>();
        evenNumberOfDates.put(LocalDate.of(2013, 1, 1), true);
        evenNumberOfDates.put(LocalDate.of(2014, 1, 1), false);
        evenNumberOfDates.put(LocalDate.of(2014, 2, 1), false);
        evenNumberOfDates.put(LocalDate.of(2015, 3, 1), true);

        RangeMap<LocalDate, Boolean> rangeMap = RangeUtils.toRangeMap(evenNumberOfDates, LocalDate.of(2016, 1, 1));
        Assert.assertTrue(rangeMap.span().hasUpperBound());
        Assert.assertEquals(LocalDate.of(2016, 1, 1), rangeMap.span().upperEndpoint());
        Assert.assertEquals(evenNumberOfDates.size(), rangeMap.asMapOfRanges().size());
        Assert.assertTrue("Last date exists", rangeMap.get(LocalDate.of(2016, 1, 1)));
        Assert.assertNull("Future date does not exist", rangeMap.get(LocalDate.of(2020, 1, 1)));

        rangeMap = RangeUtils.toRangeMap(evenNumberOfDates);
        Assert.assertFalse(rangeMap.span().hasUpperBound());
        Assert.assertEquals(evenNumberOfDates.size(), rangeMap.asMapOfRanges().size());
        Assert.assertTrue("Future date exists", rangeMap.get(LocalDate.of(2020, 1, 1)));
    }

    @Test
    public void testSplitRange() throws Exception {

    }

    @Test
    public void testToRanges() throws Exception {

    }

    @Test
    public void testGetRangeSet() throws Exception {

    }

    @Test
    public void testIntersection() throws Exception {

    }
}