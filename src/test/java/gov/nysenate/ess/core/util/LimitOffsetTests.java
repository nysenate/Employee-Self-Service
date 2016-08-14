package gov.nysenate.ess.core.util;

import gov.nysenate.ess.core.annotation.ProperTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.Assert.*;

@Category(ProperTest.class)
public class LimitOffsetTests
{
    /** Limit list will return a subset of an array. */
    @Test
    public void testLimitList() throws Exception {
        List<Integer> numbers = IntStream.range(0, 100).boxed().collect(Collectors.toList());
        assertEquals(10, LimitOffset.limitList(numbers, LimitOffset.TEN).size());
        assertEquals(0, (long) LimitOffset.limitList(numbers, new LimitOffset(10, 0)).get(0));
        assertEquals(9, (long) LimitOffset.limitList(numbers, LimitOffset.TEN).get(9));
        assertEquals(10, (long) LimitOffset.limitList(numbers, new LimitOffset(10,11)).get(0));
        assertEquals(1, LimitOffset.limitList(numbers, LimitOffset.ONE).size());
    }

    /** Limit list on a zero limit will return the whole list. */
    @Test
    public void testLimitList_noLimit() throws Exception {
        List<Integer> numbers = IntStream.range(0, 100).boxed().collect(Collectors.toList());
        assertEquals(100, LimitOffset.limitList(numbers, new LimitOffset(0,0)).size());
        assertEquals(100, LimitOffset.limitList(numbers, new LimitOffset(0,10)).size());
    }

    /** The next method should return a new limit offset that is offset by the prev limit. */
    @Test
    public void testNext() throws Exception {
        LimitOffset lim1 = new LimitOffset(10, 0);
        assertEquals(new LimitOffset(10, 11), lim1.next());
        assertEquals(new LimitOffset(10, 21), lim1.next().next());
        // Calling next on a no limit will offset to the max integer value
        assertEquals(new LimitOffset(0, Integer.MAX_VALUE), new LimitOffset(0, 10).next());
        // Prevent overflow
        LimitOffset limOverflow =  new LimitOffset(0, Integer.MAX_VALUE);
        assertEquals(new LimitOffset(0, Integer.MAX_VALUE), limOverflow.next());
    }

    /** A LimitOffset will have a limit when the limit is not 0. */
    @Test
    public void testHasLimit() throws Exception {
        assertTrue(LimitOffset.TEN.hasLimit());
        assertFalse(LimitOffset.ALL.hasLimit());
        assertFalse(new LimitOffset(0,0).hasLimit());
        assertFalse(new LimitOffset(0, 100).hasLimit());
    }

    @Test
    public void testHasOffset() throws Exception {
        // Typical case
        assertTrue(new LimitOffset(0, 10).hasOffset());
        assertFalse(new LimitOffset(0, 0).hasOffset());
        assertFalse(new LimitOffset(0, -1).hasOffset());
    }

    /** Offset start should always be positive */
    @Test
    public void testGetOffsetStart() throws Exception {
        // Typical
        assertEquals(10, new LimitOffset(1, 10).getOffsetStart());
        // Zero offset becomes 1
        assertEquals(1, new LimitOffset(1, 0).getOffsetStart());
        // negative offset will return 1
        assertEquals(1, new LimitOffset(0, -1).getOffsetStart());
        assertEquals(1, new LimitOffset(1, -1).getOffsetStart());
        // max offset
        assertEquals(Integer.MAX_VALUE, new LimitOffset(1, Integer.MAX_VALUE).getOffsetStart());
    }

    /** The offset should always be positive. */
    @Test
    public void testGetOffsetEnd() throws Exception {
        // Typical
        assertEquals(10, new LimitOffset(1, 10).getOffsetEnd());
        // negative offset will return max offset
        assertEquals(Integer.MAX_VALUE, new LimitOffset(0, -1).getOffsetEnd());
    }

    /** negative limits throw exception */
    @Test(expected = IllegalArgumentException.class)
    public void testGetLimit_negative() throws Exception {
        new LimitOffset(-1, 0).getLimit();
    }

    /** Zero limits are just zero. */
    @Test
    public void testGetLimit_zero() throws Exception {
        assertEquals(0, new LimitOffset(0, 0).getLimit());
    }

    @Test
    public void testEquals() throws Exception {
        LimitOffset lim1 = new LimitOffset(0,0);
        assertEquals(lim1, new LimitOffset(0,0));

        LimitOffset lim2 = new LimitOffset(10,0);
        assertEquals(lim2, new LimitOffset(10,0));

        LimitOffset lim3 = new LimitOffset(100,10);
        assertEquals(lim3, new LimitOffset(100,10));

        LimitOffset lim4 = new LimitOffset(100,-1);
        assertEquals(lim4, new LimitOffset(100,-1));
    }

    /** Hash codes are unique for the most part. */
    @Test
    public void testHashCode() throws Exception {
        for (int i = 1; i < 1000; i++) {
            assertEquals(new LimitOffset(i, i).hashCode(), new LimitOffset(i, i).hashCode());
            assertNotEquals(new LimitOffset(i, i).hashCode(), new LimitOffset(i - 1, i).hashCode());
        }
    }
}