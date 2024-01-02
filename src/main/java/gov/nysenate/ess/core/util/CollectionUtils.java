package gov.nysenate.ess.core.util;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class CollectionUtils {
    private CollectionUtils() {}

    public static <K, V> Map<K, V> keysToMap(Collection<K> keys, Function<K, V> keyToValueFunction) {
        return keys.stream().collect(Collectors.toMap(Function.identity(), keyToValueFunction));
    }

    public static <K, V> Map<K, V> valuesToMap(Collection<V> values, Function<V, K> valueToKeyFunction) {
        return values.stream().collect(Collectors.toMap(valueToKeyFunction, Function.identity()));
    }
}
