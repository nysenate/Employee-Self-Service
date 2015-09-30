package gov.nysenate.common;

import com.google.common.collect.Range;
import com.google.common.collect.RangeMap;
import com.google.common.collect.TreeRangeMap;
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

    @Test
    public void testIntersects() throws Exception {
        Range<Integer> r1 = Range.closedOpen(1, 3);
        Range<Integer> r2 = Range.openClosed(2, 4);
        Range<Integer> r3 = Range.closedOpen(3, 4);
        Range<Integer> r4 = Range.closedOpen(2, 4);
        Range<Integer> r5 = Range.closedOpen(4, 5);

        Assert.assertTrue(RangeUtils.intersects(r1, r2));
        Assert.assertFalse(RangeUtils.intersects(r1, r3));
        Assert.assertTrue(RangeUtils.intersects(r1, r4));
        Assert.assertFalse(RangeUtils.intersects(r1, r5));
    }
    
    @Test
    public void removeIntersectingTest() {
        Range<Integer> r1 = Range.closedOpen(1, 3);
        Range<Integer> r2 = Range.closedOpen(2, 4);
        Range<Integer> r3 = Range.closedOpen(3, 4);
        Range<Integer> r4 = Range.closedOpen(2, 4);
        Range<Integer> r5 = Range.closedOpen(4, 5);
        Object derpus = new Object();
        RangeMap<Integer, Object> rangeMap = TreeRangeMap.create();
        rangeMap.put(r1, derpus);
        rangeMap.put(r3, derpus);
        RangeUtils.removeIntersecting(rangeMap, r2);
        Assert.assertTrue(rangeMap.asMapOfRanges().isEmpty());
    }
}