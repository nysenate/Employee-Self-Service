package gov.nysenate.common;

import com.google.common.collect.*;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Map.Entry;

public class RangeUtils
{
    /**
     * Creates a range map out of a map with comparable keys
     * This essentially transforms each key into a range of [current key, next highest key)
     * The value with the highest key will be assigned a range of [highest key, end key] with end key being provided as an arg
     * @param map SortedMap<K, V>
     * @param endKey K - Set as the closed right bound for the range of the highest key
     * @param <K> K
     * @param <V> V
     * @return RangeMap<K, V>
     */
    public static <K extends Comparable<? super K>, V> RangeMap<K, V> toRangeMap(SortedMap<K, V> map, K endKey) {
        RangeMap<K, V> rangeMap = TreeRangeMap.create();

        Entry<K, V> lastEntry = null;
        for (Entry<K, V> entry : map.entrySet()) {
            if (lastEntry != null) {
                rangeMap.put(Range.closedOpen(lastEntry.getKey(), entry.getKey()), lastEntry.getValue());
            }
            lastEntry = entry;
        }
        if (lastEntry != null) {
            if (endKey == null) {
                rangeMap.put(Range.atLeast(lastEntry.getKey()), lastEntry.getValue());
            }
            else {
                rangeMap.put(Range.closed(lastEntry.getKey(), endKey), lastEntry.getValue());
            }
        }
        return rangeMap;
    }

    /**
     * Overload with no end date (infinity).
     * @see #toRangeMap(SortedMap, Comparable)
     */
    public static <K extends Comparable<? super K>, V> RangeMap<K, V> toRangeMap(SortedMap<K, V> map) {
        return toRangeMap(map, null);
    }

    /**
     * Splits a range into several smaller ranges on each value of splitValues
     * e.g. [1, 5) would be split into [1,2), [2,4), and [4,5) for split values 2, 4
     * @param range Range<K>
     * @param splitValues Collection<K>
     * @param <K>
     * @return TreeSet<Range<K>>
     */
    public static <K extends Comparable<? super K>> List<Range<K>> splitRange(Range<K> range, Collection<K> splitValues) {
        TreeSet<K> validSplitValues = splitValues.stream()
                // Cannot use values outside of the range or values equal to a closed boundpoint
                .filter(value -> range.contains(value) &&
                        (!range.hasLowerBound() || range.lowerBoundType() == BoundType.OPEN ||
                                !range.lowerEndpoint().equals(value)) &&
                        (!range.hasUpperBound() || range.upperBoundType() == BoundType.OPEN ||
                                !range.upperEndpoint().equals(value)))
                .collect(Collectors.toCollection(TreeSet::new));

        List<Range<K>> ranges = new ArrayList<>();
        if (validSplitValues.isEmpty()) {
            ranges.add(range);
            return ranges;
        }

        Iterator<K> backIterator = validSplitValues.iterator();
        Iterator<K> frontIterator = validSplitValues.iterator();
        if (frontIterator.hasNext()) {
            ranges.add(range.hasLowerBound()
                    ? Range.range(range.lowerEndpoint(), range.lowerBoundType(), frontIterator.next(), BoundType.OPEN)
                    : Range.lessThan(frontIterator.next()));
        }

        while (backIterator.hasNext()) {
            if (frontIterator.hasNext()) {
                ranges.add(Range.closedOpen(backIterator.next(), frontIterator.next()));
            } else {
                ranges.add(range.hasUpperBound()
                        ? Range.range(backIterator.next(), BoundType.CLOSED, range.upperEndpoint(), range.upperBoundType())
                        : Range.atLeast(backIterator.next()));
            }
        }

        return ranges;
    }

    /**
     * Creates a range set out of a set with comparable keys
     * This essentially transforms each element into a range of [current ele, next highest ele)
     * The value with the highest element will be assigned a range of [highest ele, end ele] with end key being provided as an arg
     * @param set SortedSet<E>
     * @param endElement E
     * @param <E> E
     * @return TreeSet<E>
     */
    public static <E extends Comparable<? super E>> TreeSet<Range<E>> toRanges(SortedSet<E> set, E endElement) {
        TreeSet<Range<E>> rangeSet = new TreeSet<>();

        Iterator<E> backIterator = set.iterator();
        Iterator<E> frontIterator = set.iterator();
        if (frontIterator.hasNext()) {
            frontIterator.next();
        }

        while (backIterator.hasNext()) {
            rangeSet.add(frontIterator.hasNext()
                    ? Range.openClosed(backIterator.next(), frontIterator.next())
                    : Range.closed(backIterator.next(), endElement));
        }

        return rangeSet;
    }

    /**
     * Generates a range set from the keys of the given range map
     * @param rangeMap RangeMap
     * @param <K> Class
     * @return RangeSet<K>
     */
    public static <K extends Comparable<? super K>> RangeSet<K> getRangeSet(RangeMap<K, ?> rangeMap) {
        RangeSet<K> rangeSet = TreeRangeSet.create();
        rangeMap.asMapOfRanges().keySet().forEach(rangeSet::add);
        return rangeSet;
    }

    public static <T extends Comparable<? super T>> RangeSet<T> intersection(RangeSet<T> lhs, RangeSet<T> rhs) {
        RangeSet<T> intersection = TreeRangeSet.create();
        rhs.asRanges().forEach(range -> intersection.addAll(lhs.subRangeSet(range)));
        return intersection;
    }

}