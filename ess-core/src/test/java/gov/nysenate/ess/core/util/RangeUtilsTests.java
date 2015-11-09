package gov.nysenate.ess.core.util;

import com.google.common.collect.*;
import gov.nysenate.ess.core.annotation.ProperTest;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.*;

import static org.junit.Assert.*;

@ProperTest
public class RangeUtilsTests
{
    private Logger logger = LoggerFactory.getLogger(RangeUtilsTests.class);

    @Test
    public void testToRangeMap() throws Exception {
        TreeMap<LocalDate, Boolean> evenNumberOfDates = new TreeMap<>();
        evenNumberOfDates.put(LocalDate.ofYearDay(2013, 1), true);
        evenNumberOfDates.put(LocalDate.ofYearDay(2014, 1), false);
        evenNumberOfDates.put(LocalDate.ofYearDay(2015, 1), false);
        evenNumberOfDates.put(LocalDate.ofYearDay(2016, 1), true);

        // Test toRangeMap with upper bound
        RangeMap<LocalDate, Boolean> rangeMap = RangeUtils.toRangeMap(evenNumberOfDates, LocalDate.ofYearDay(2017, 1));
        assertTrue(rangeMap.span().hasUpperBound());
        assertEquals(LocalDate.ofYearDay(2017, 1), rangeMap.span().upperEndpoint());
        assertEquals(evenNumberOfDates.size(), rangeMap.asMapOfRanges().size());
        assertNull("pre range start doesn't exist", rangeMap.get(LocalDate.ofYearDay(2013, 1).minusDays(1)));
        assertEquals("range start correct", true, rangeMap.get(LocalDate.ofYearDay(2013, 1)));
        assertEquals("range body correct", true, rangeMap.get(LocalDate.ofYearDay(2014, 1).minusDays(1)));
        assertEquals("range body correct", false, rangeMap.get(LocalDate.ofYearDay(2014, 1)));
        assertEquals("Last date correct", true, rangeMap.get(LocalDate.ofYearDay(2017, 1)));
        assertNull("Future date does not exist", rangeMap.get(LocalDate.ofYearDay(2020, 1)));

        // toRangeMap without upper bound
        rangeMap = RangeUtils.toRangeMap(evenNumberOfDates);
        assertFalse(rangeMap.span().hasUpperBound());
        assertEquals(evenNumberOfDates.size(), rangeMap.asMapOfRanges().size());
        assertEquals("Future date exists", rangeMap.get(LocalDate.ofYearDay(2016, 1)), true);
    }

    @Test
    public void testSplitRange() throws Exception {
        Range<Integer> intRange = Range.open(1, 4);
        List<Integer> splitValues = Arrays.asList(0, 1, 2, 3, 4, 5);
        List<Range<Integer>> splitResult = RangeUtils.splitRange(intRange, splitValues);
        assertEquals("correct number of ranges", 3, splitResult.size());
        assertEquals("Open lower bound split correct", Range.open(1, 2), splitResult.get(0));
        assertEquals("Inside split correct", Range.closedOpen(2, 3), splitResult.get(1));
        assertEquals("Upper bound split correct", Range.closedOpen(3, 4), splitResult.get(2));
    }

    @Test
    public void testToRanges() throws Exception {
        TreeSet<Integer> intSet = new TreeSet<>(Arrays.asList(1, 2, 3));
        List<Range<Integer>> rangeResult = RangeUtils.toRanges(intSet, 4, BoundType.CLOSED);
        assertEquals("correct number of ranges", 3, rangeResult.size());
        assertEquals("first range correct", Range.closedOpen(1, 2), rangeResult.get(0));
        assertEquals("second range correct", Range.closedOpen(2, 3), rangeResult.get(1));
        assertEquals("third range correct", Range.closed(3, 4), rangeResult.get(2));
    }

    @Test
    public void testGetRangeSet() throws Exception {
        List<Range<Integer>> ranges = Arrays.asList(Range.closedOpen(1, 3), Range.closedOpen(4, 5),
                Range.closed(5, 7), Range.closed(5, 6));
        RangeSet<Integer> targetRangeSet = TreeRangeSet.create();
        ranges.forEach(targetRangeSet::add);

        Object derpus = new Object();
        RangeMap<Integer, Object> rangeMap = TreeRangeMap.create();
        ranges.stream().forEach(range -> rangeMap.put(range, derpus));

        RangeSet<Integer> resultRangeSet = RangeUtils.getRangeSet(rangeMap);

        assertEquals(targetRangeSet, resultRangeSet);
    }

    @Test
    public void testIntersection() throws Exception {
        RangeSet<Integer> rs1 = TreeRangeSet.create();
        RangeSet<Integer> rs2 = TreeRangeSet.create();
        rs1.add(Range.closedOpen(1, 3));
        rs1.add(Range.closedOpen(5, 7));
        rs2.add(Range.closedOpen(2, 4));
        rs2.add(Range.closedOpen(6, 8));

        RangeSet<Integer> targetRs = TreeRangeSet.create();
        targetRs.add(Range.closedOpen(2, 3));
        targetRs.add(Range.closedOpen(6, 7));

        RangeSet<Integer> intersection = RangeUtils.intersection(rs1, rs2);

        assertEquals(targetRs, intersection);
    }

    @Test
    public void testIntersects() throws Exception {
        Range<Integer> r1 = Range.closedOpen(1, 3);
        Range<Integer> r2 = Range.openClosed(2, 4);
        Range<Integer> r3 = Range.closedOpen(3, 4);
        Range<Integer> r4 = Range.closedOpen(2, 4);
        Range<Integer> r5 = Range.closedOpen(4, 5);

        assertTrue(RangeUtils.intersects(r1, r2));
        assertFalse(RangeUtils.intersects(r1, r3));
        assertTrue(RangeUtils.intersects(r1, r4));
        assertFalse(RangeUtils.intersects(r1, r5));
    }
    
    @Test
    public void removeIntersectingTest() {
        Range<Integer> r1 = Range.closedOpen(1, 3);
        Range<Integer> r2 = Range.closedOpen(2, 4);
        Range<Integer> r3 = Range.closedOpen(3, 4);
        Object derpus = new Object();
        RangeMap<Integer, Object> rangeMap = TreeRangeMap.create();
        rangeMap.put(r1, derpus);
        rangeMap.put(r3, derpus);
        RangeUtils.removeIntersecting(rangeMap, r2);
        assertTrue(rangeMap.asMapOfRanges().isEmpty());
    }
}