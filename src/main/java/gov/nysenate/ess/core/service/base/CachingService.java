package gov.nysenate.ess.core.service.base;

import com.google.common.eventbus.Subscribe;
import gov.nysenate.ess.core.model.cache.CacheEvictEvent;
import gov.nysenate.ess.core.model.cache.CacheEvictIdEvent;
import gov.nysenate.ess.core.model.cache.CacheWarmEvent;
import gov.nysenate.ess.core.model.cache.ContentCache;

/**
 * Defines methods that are required to manage a service that depends on one or more caches
 * @param <ContentId>
 */
public interface CachingService<ContentId>
{
    /**
     * @return A ContentCache enum specifying which cache this is
     */
    ContentCache getCacheType();

    /**
     * Evicts a single item from the cache based on the given content id
     */
    void evictContent(ContentId contentId);

    /**
     * Clears all the cache entries from cache.
     */
    void evictCache();

    /**
     * Pre-fetch a subset of currently active data and store it in the cache.
     * If no override is provided, just evict the cache
     */
    default void warmCache() {
        evictCache();
    }

    /**
     * If a CacheEvictEvent is sent out on the event bus, the caching service
     * should check to see if it has any affected caches and clear them.
     *
     * @param evictEvent CacheEvictEvent
     */
    @Subscribe
    default void handleCacheEvictEvent(CacheEvictEvent evictEvent) {
        if (evictEvent.affects(getCacheType())) {
            evictCache();
        }
    }

    /**
     * Intercept an evict Id event and evict the specified content
     * if the caching service has any of the affected caches
     * @param evictIdEvent CacheEvictIdEvent
     */
    @Subscribe
    default void handleCacheEvictIdEvent(CacheEvictIdEvent<ContentId> evictIdEvent) {
        if (evictIdEvent.affects(getCacheType())) {
            evictContent(evictIdEvent.getContentId());
        }
    }

    /**
     * If a CacheWarmEvent is sent out on the event bus, the caching service
     * should check to if it has any affected caches and warm them.
     *
     * @param warmEvent CacheWarmEvent
     */
    @Subscribe
    default void handleCacheWarmEvent(CacheWarmEvent warmEvent) {
        if (warmEvent.affects(getCacheType())) {
            warmCache();
        }
    }
}
