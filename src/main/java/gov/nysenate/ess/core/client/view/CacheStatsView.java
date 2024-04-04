package gov.nysenate.ess.core.client.view;

import gov.nysenate.ess.core.client.view.base.ViewObject;

public record CacheStatsView(String cacheName, long size, int capacity, long putCount, long removeCount,
                             long evictedCount, long expiredCount, long hitCount,
                             long missCount, float hitRatio) implements ViewObject {

    public static CacheStatsView defaultView(String cacheName) {
        return new CacheStatsView(cacheName, -1, -1, -1, -1, -1, -1, -1, -1, -1);
    }

    @Override
    public String getViewType() {
        return "cache-stats";
    }
}
