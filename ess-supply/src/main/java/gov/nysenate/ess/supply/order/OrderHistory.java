package gov.nysenate.ess.supply.order;

import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.Ordering;

import java.time.LocalDateTime;
import java.util.SortedMap;

/**
 * A collection of order versions
 */
public final class OrderHistory {

    private final ImmutableSortedMap<LocalDateTime, OrderVersion> orderVersionMap;

    private OrderHistory(ImmutableSortedMap<LocalDateTime, OrderVersion> orderVersionMap) {
        this.orderVersionMap = orderVersionMap;
    }

    /** Static constructors */

    public static OrderHistory of(LocalDateTime modifyDateTime, OrderVersion version) {
        return new OrderHistory(ImmutableSortedMap.of(modifyDateTime, version));
    }

    public static OrderHistory of(SortedMap<LocalDateTime, OrderVersion> orderVersionMap) {
        return new OrderHistory(ImmutableSortedMap.copyOf(orderVersionMap));
    }

    public static OrderHistory of(ImmutableSortedMap<LocalDateTime, OrderVersion> orderVersionMap) {
        return new OrderHistory(orderVersionMap);
    }

    /** Methods */

    public OrderVersion get(LocalDateTime orderSubmittedDateTime) {
        return orderVersionMap.get(orderSubmittedDateTime);
    }

    protected OrderVersion current() {
        return orderVersionMap.get(orderVersionMap.lastKey());
    }

    protected OrderHistory addVersion(LocalDateTime modifiedDateTime, OrderVersion rejected) {
        ImmutableSortedMap versions = new ImmutableSortedMap.Builder<LocalDateTime, OrderVersion>(Ordering.natural())
                .putAll(orderVersionMap).put(modifiedDateTime, rejected).build();
        return OrderHistory.of(versions);
    }

    protected int size() {
        return orderVersionMap.size();
    }



}
