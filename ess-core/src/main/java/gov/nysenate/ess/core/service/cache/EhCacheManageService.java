package gov.nysenate.ess.core.service.cache;

import net.sf.ehcache.Cache;
import net.sf.ehcache.config.CacheConfiguration;

public interface EhCacheManageService
{
    Cache registerEternalCache(String cacheName);

    Cache registerTimeBasedCache(String cacheName, Long secondsToExpiration);

    Cache registerMemoryBasedCache(String cacheName, Long megabytes);

    Cache registerCountBasedCache(String cacheName, Integer maxEntries);

    Cache registerCustomCache(CacheConfiguration config);

    void clearAllCaches();

    boolean isWarmOnStartup();
}