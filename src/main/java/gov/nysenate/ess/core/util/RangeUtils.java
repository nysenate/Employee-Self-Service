package gov.nysenate.ess.core.util;

import com.google.common.collect.*;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static gov.nysenate.ess.core.util.DateUtils.getLocalDateDiscreteDomain;
import static java.util.Map.Entry;

public class RangeUtils
{
    /**
     * Creates a range map out of a map with comparable keys
     * This essentially transforms each key into a range of [current key, next highest key)
     * The value with the highest key will be assigned a range of [highest key, end key] with end key being provided as an arg
     * @param map SortedMap<K, V>
     * @param endKey K - Set as the closed right bound for the range of the highest key
     * @param <Key> K
     * @param <Value> V
     * @return RangeMap<K, V>
     */
    public static <Key extends Comparable<? super Key>, Value> RangeMap<Key, Value> toRangeMap(SortedMap<Key, Value> map, Key endKey) {
        RangeMap<Key, Value> rangeMap = TreeRangeMap.create();

        Entry<Key, Value> lastEntry = null;
        for (Entry<Key, Value> entry : map.entrySet()) {
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
     * The value with the highest element will be assigned a range of [highest ele, end ele) or [highest ele, end ele]
     * with end key and bound type provided as an argument
     * @param set SortedSet<E>
     * @param endElement E
     * @param <E> E
     * @return TreeSet<E>
     */
    public static <E extends Comparable<? super E>> List<Range<E>> toRanges(SortedSet<E> set, E endElement, BoundType upperBoundType) {
        List<Range<E>> ranges = new ArrayList<>();

        Iterator<E> backIterator = set.iterator();
        Iterator<E> frontIterator = set.iterator();
        if (frontIterator.hasNext()) {
            frontIterator.next();
        }

        while (backIterator.hasNext()) {
            ranges.add(frontIterator.hasNext()
                    ? Range.closedOpen(backIterator.next(), frontIterator.next())
                    : Range.range(backIterator.next(), BoundType.CLOSED, endElement, upperBoundType));
        }

        return ranges;
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

    /**
     * Given a tree map that maps effective values to keys and a predicate for the value type,
     * return a range set containing all keys with effective values that satisfy the given predicate
     *
     * @param valMap TreeMap<K, V>
     * @param valTester Predicate<V>
     * @param <K> Key - must be Comparable
     * @param <V> Value
     * @return RangeSet<K>
     */
    public static <K extends Comparable<? super K>, V> RangeSet<K> getEffectiveRanges(
            SortedMap<K, V> valMap, Predicate<? super V> valTester) {

        RangeSet<K> effectiveRange = TreeRangeSet.create();
        RangeUtils.toRangeMap(valMap)
                .asMapOfRanges().entrySet().stream()
                .filter(entry -> valTester.test(entry.getValue()))
                .map(Map.Entry::getKey)
                .forEach(effectiveRange::add);
        return effectiveRange;
    }

    /**
     * Get a range set that contains the intersection of the two given range sets
     * @param lhs RangeSet<T>
     * @param rhs RangeSet<T>
     * @param <T> Class<T>
     * @return RangeSet<T>
     */
    public static <T extends Comparable<? super T>> RangeSet<T> intersection(RangeSet<T> lhs, RangeSet<T> rhs) {
        RangeSet<T> intersection = TreeRangeSet.create();
        rhs.asRanges().stream()
                .map(lhs::subRangeSet)
                .forEach(intersection::addAll);
        return intersection;
    }

    /**
     * Get a range set that contains the intersection of all of the given range sets
     * @param rangeSets Collection<RangeSet<T>>
     * @param <T>
     * @return RangeSet<T>
     */
    public static <T extends Comparable<? super T>> RangeSet<T> intersection(Collection<RangeSet<T>> rangeSets) {
        if (rangeSets.isEmpty()) {
            return TreeRangeSet.create();
        }
        return rangeSets.stream()
                .reduce(ImmutableRangeSet.of(Range.all()), RangeUtils::intersection);
    }

    /**
     * Returns true iff the two given ranges intersect
     * @param lhs Range<T>
     * @param rhs Range<T>
     * @param <T> Class<T>
     * @return boolean
     */
    public static <T extends Comparable<? super T>> boolean intersects(Range<T> lhs, Range<T> rhs) {
        return lhs.isConnected(rhs) && !lhs.intersection(rhs).isEmpty();
    }

    /**
     * Returns true iff the given range set intersects the given range
     * @param rangeSet RangeSet<T>
     * @param range Range<T>
     * @param <T> Class<T>
     * @return boolean
     */
    public static <T extends Comparable<? super T>> boolean intersects(RangeSet<T> rangeSet, Range<T> range) {
        return !rangeSet.complement().encloses(range);
    }

    /**
     * Returns an iterator that produces values of the specified discrete class that are in the given range, in default order
     * @param range Range<T>
     * @param domain DiscreteDomain<T> - a discrete domain implementation that supports the class of the range
     * @param <T> Class<T>
     * @return Iterator<T>
     */
    public static <T extends Comparable<? super T>> Iterator<T> getRangeIterator(Range<T> range, DiscreteDomain<T> domain) {
        return ContiguousSet.create(range, domain).iterator();
    }

    /**
     * An implementation of
     * @see #getRangeIterator(Range, DiscreteDomain)
     * that uses the LocalDate discrete domain
     * @param dateRange Range<LocalDate>
     * @return Iterator<LocalDate>
     */
    public static Iterator<LocalDate> getDateRangeIterator(Range<LocalDate> dateRange) {
        return getRangeIterator(dateRange, getLocalDateDiscreteDomain());
    }

    /**
     * An implementation of
     * @see #getRangeIterator(Range, DiscreteDomain)
     * that uses the Integer discrete domain
     * @param intRange Range<Integer>
     * @return Iterator<Integer>
     */
    public static Iterator<Integer> getCounter(Range<Integer> intRange) {
        return getRangeIterator(intRange, DiscreteDomain.integers());
    }

    /**
     * A default implementation of
     * @see #getCounter(Range)
     * that iterates over all integers greater than 0
     * @return Iterator<Integer>
     */
    public static Iterator<Integer> getCounter() {
        return getCounter(Range.greaterThan(0));
    }

}