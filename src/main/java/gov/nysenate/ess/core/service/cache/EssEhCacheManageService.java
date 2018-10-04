package gov.nysenate.ess.core.service.cache;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.MemoryUnit;
import net.sf.ehcache.statistics.StatisticsGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/** {@inheritDoc} */
@Service
public class EssEhCacheManageService implements EhCacheManageService
{
    private static final Logger logger = LoggerFactory.getLogger(EssEhCacheManageService.class);

    @Autowired CacheManager cacheManager;

    @Value("${cache.warm.onstartup:false}")
    protected boolean warmOnStartup;

    @PreDestroy
    public void destroy() {
        destroyAllCaches();
    }

    /** {@inheritDoc} */
    @Override
    public Cache registerEternalCache(String cacheName) {
        return registerCustomCache(getDefaultConfig().name(cacheName).eternal(true));
    }

    /** {@inheritDoc} */
    @Override
    public Cache registerTimeBasedCache(String cacheName, Long secondsToExpiration) {
        return registerCustomCache(getDefaultConfig().name(cacheName).timeToLiveSeconds(secondsToExpiration));
    }

    /** {@inheritDoc} */
    @Override
    public Cache registerMemoryBasedCache(String cacheName, Long megabytes) {
        return registerCustomCache(getDefaultConfig().name(cacheName)
            .maxBytesLocalHeap(megabytes, MemoryUnit.MEGABYTES));
    }

    /** {@inheritDoc} */
    @Override
    public Cache registerCountBasedCache(String cacheName, Integer maxEntries) {
        return registerCustomCache(getDefaultConfig().name(cacheName).maxEntriesLocalHeap(maxEntries));
    }

    /** {@inheritDoc} */
    @Override
    public Cache registerCustomCache(CacheConfiguration config) {
        Cache cache = new Cache(config);
        cacheManager.addCache(cache);
        return cache;
    }

    /** {@inheritDoc} */
    @Override
    public Set<StatisticsGateway> getCacheStatistics() {
        return Arrays.stream(cacheManager.getCacheNames())
                .map(cacheManager::getCache)
                .map(Cache::getStatistics)
                .collect(Collectors.toSet());
    }

    /** {@inheritDoc} */
    @Override
    public void clearAllCaches() {
        for (String cacheName : cacheManager.getCacheNames()) {
            logger.debug("Clearing out {} cache.", cacheName);
            cacheManager.getCache(cacheName).removeAll();
        }
    }

    /** {@inheritDoc} */
    @Override
    public void destroyAllCaches() {
        cacheManager.removeAllCaches();
    }

    public boolean isWarmOnStartup() {
        return warmOnStartup;
    }

    /**
     * Get a copy of the default cache configuration.
     */
    private CacheConfiguration getDefaultConfig() {
        return cacheManager.getConfiguration().getDefaultCacheConfiguration().clone();
    }
}