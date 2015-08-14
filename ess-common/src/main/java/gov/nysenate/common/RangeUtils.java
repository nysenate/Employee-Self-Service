package gov.nysenate.common;

import com.google.common.collect.Range;
import com.google.common.collect.RangeMap;
import com.google.common.collect.TreeRangeMap;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import static java.util.Map.Entry;

public class RangeUtils {

    /**
     * Creates a range map out of a map with comparable keys
     * This essentially transforms each key into a range of [current key, next highest key)
     * The value with the highest key will be assigned a range of [highest key, end key] with end key being provided as an arg
     * @param map Map<K, V>
     * @param endKey K - Set as the closed right bound for the range of the highest key
     * @param <K> K
     * @param <V> V
     * @return RangeMap<K, V>
     */
    public static <K extends Comparable<? super K>, V> RangeMap<K, V> toRangeMap(Map<K, V> map, K endKey) {
        Set<Entry<K, V>> entries;
        if (map instanceof TreeMap) {
            entries = map.entrySet();
        } else {
            entries = new TreeSet<>((a, b) -> a.getKey().compareTo(b.getKey()));
            entries.addAll(map.entrySet());
        }

        RangeMap<K, V> rangeMap = TreeRangeMap.create();

        Entry<K, V> lastEntry = null;
        for (Entry<K, V> entry : entries) {
            if (lastEntry != null) {
                rangeMap.put(Range.closedOpen(lastEntry.getKey(), entry.getKey()), lastEntry.getValue());
            }
            lastEntry = entry;
        }
        if (lastEntry != null) {
            rangeMap.put(Range.closed(lastEntry.getKey(), endKey), lastEntry.getValue());
        }

        return rangeMap;
    }
}