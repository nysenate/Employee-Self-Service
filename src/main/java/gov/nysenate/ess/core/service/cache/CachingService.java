package gov.nysenate.ess.core.service.cache;

import gov.nysenate.ess.core.config.InheritedService;
import gov.nysenate.ess.core.model.cache.CacheType;
import org.ehcache.Cache;

/**
 * Defines methods that are required to manage a service that depends on one or more caches.
 */
@InheritedService
public abstract class CachingService<Key, Value> {
    // Cache creation is left up to implementations.
    protected Cache<Key, Value> cache;

    /**
     * @return Specifies which cache this is.
     */
    public abstract CacheType cacheType();

    protected abstract void evictContent(String key);

    /**
     * Clears all the cache entries from cache.
     */
    public abstract void clearCache(boolean warmCache);
}
