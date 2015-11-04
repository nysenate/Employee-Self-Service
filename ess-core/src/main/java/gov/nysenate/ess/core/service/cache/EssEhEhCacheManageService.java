package gov.nysenate.ess.core.service.cache;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.MemoryUnit;
import net.sf.ehcache.config.SizeOfPolicyConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import static net.sf.ehcache.config.SizeOfPolicyConfiguration.MaxDepthExceededBehavior.ABORT;

@Service
public class EssEhEhCacheManageService implements EhCacheManageService
{
    private static final Logger logger = LoggerFactory.getLogger(EssEhEhCacheManageService.class);

    @Autowired CacheManager cacheManager;

    @Value("${cache.warm.onstartup:false}")
    protected boolean warmOnStartup;

    @Override
    public Cache registerEternalCache(String cacheName) {
        return registerCustomCache(new CacheConfiguration().name(cacheName).eternal(true));
    }

    @Override
    public Cache registerTimeBasedCache(String cacheName, Long secondsToExpiration) {
        return registerCustomCache(new CacheConfiguration().name(cacheName).timeToLiveSeconds(secondsToExpiration));
    }

    @Override
    public Cache registerMemoryBasedCache(String cacheName, Long megabytes) {
        return registerCustomCache(new CacheConfiguration().name(cacheName)
            .maxBytesLocalHeap(megabytes, MemoryUnit.MEGABYTES));
    }

    @Override
    public Cache registerCountBasedCache(String cacheName, Integer maxEntries) {
        return registerCustomCache(new CacheConfiguration().name(cacheName).maxEntriesLocalHeap(maxEntries));
    }

    @Override
    public Cache registerCustomCache(CacheConfiguration config) {
        config.sizeOfPolicy(defaultSizeOfPolicy());
        Cache cache = new Cache(config);
        cacheManager.addCache(cache);
        return cache;
    }

    @Override
    public void clearAllCaches() {
        for (String cacheName : cacheManager.getCacheNames()) {
            logger.debug("Clearing out {} cache.", cacheName);
            cacheManager.getCache(cacheName).removeAll();
        }
    }

    private SizeOfPolicyConfiguration defaultSizeOfPolicy() {
        return new SizeOfPolicyConfiguration().maxDepth(50000).maxDepthExceededBehavior(ABORT);
    }

    public boolean isWarmOnStartup() {
        return warmOnStartup;
    }
}