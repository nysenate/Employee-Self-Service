package gov.nysenate.ess.core.service.cache;

import net.sf.ehcache.Cache;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.statistics.StatisticsGateway;

import java.util.Set;

/**
 * A service that manages eh cache instances
 * Contains methods to register and check out caches
 * Contains methods to perform bulk management operations on all registered caches
 */
public interface EhCacheManageService
{
    /**
     * Registers an eternal cache
     * Elements in an eternal cache are never evicted unless done so explicitly
     *
     * @param cacheName String - The name that the new cache will be registered under
     * @return Cache - The registered cache
     */
    Cache registerEternalCache(String cacheName);

    /**
     * Registers a time based cache
     * Elements placed in a time based cache will be evicted after a specified amount of time
     *
     * @param cacheName String - The name that the new cache will be registered under
     * @param secondsToExpiration Long - elements will be expired this many seconds after insertion
     * @return Cache - The registered cache
     */
    Cache registerTimeBasedCache(String cacheName, Long secondsToExpiration);

    /**
     * Registers a memory based cache
     * Memory based caches are limited to a specific memory size
     *
     * @param cacheName String - The name that the new cache will be registered under
     * @param megabytes Long - The max memory usage of the cache in megabytes
     * @return Cache - The registered cache
     */
    Cache registerMemoryBasedCache(String cacheName, Long megabytes);

    /**
     * Registers a count based cache
     * Count based caches are limited to a certain number of entries
     *
     * @param cacheName String - The name that the new cache will be registered under
     * @param maxEntries Integer - The max number of elements can be stored in this cache
     * @return Cache - The registered cache
     */
    Cache registerCountBasedCache(String cacheName, Integer maxEntries);

    /**
     * Registers a custom cache using a CacheConfiguration object
     * Use this if the other cache registration methods do not fit your needs
     *
     * @param config CacheConfiguration - the desired custom cache configuration
     * @return Cache - The registered cache
     */
    Cache registerCustomCache(CacheConfiguration config);

    /**
     * @return Set<StatisticsGateway> - statistics for each cache registered through this service
     */
    Set<StatisticsGateway> getCacheStatistics();

    /**
     * Clears all caches registered through this service
     */
    void clearAllCaches();

    /**
     * Destroys all caches registered through this service
     */
    void destroyAllCaches();

    /**
     * @return boolean - true if caches should be warmed on startup
     */
    boolean isWarmOnStartup();
}