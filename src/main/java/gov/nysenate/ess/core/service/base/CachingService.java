package gov.nysenate.ess.core.service.base;

import com.google.common.eventbus.EventBus;
import gov.nysenate.ess.core.model.cache.CacheType;
import gov.nysenate.ess.core.service.cache.EssCacheManager;
import org.ehcache.Cache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;
import java.util.Map;

/**
 * Defines methods that are required to manage a service that depends on one or more caches.
 */
public abstract class CachingService<K, V> {
    private static final Logger logger = LoggerFactory.getLogger(CachingService.class);
    @Autowired
    protected EventBus eventBus;
    @Value("${cache.warm.onstartup:false}")
    protected boolean warmOnStartup;
    protected Cache<K, V> cache;

    @PostConstruct
    private void init() {
        // TODO: correct size
        this.cache = EssCacheManager.createCache(this, 100);
    }

    /**
     * @return Specifies which cache this is.
     */
    public abstract CacheType cacheType();

    // TODO: Make sure everything has some initial size
    protected Map<K, V> initialEntries() {
        return Map.of();
    }

    /**
     * Evicts a single item from the cache based on the given content id
     */
    public void evictContent(K k) {
        cache.remove(k);
    }

    public int expiryTimeSeconds() {
        return -1;
    }

    /**
     * Clears all the cache entries from cache.
     */
    public void clearCache(boolean warmCache) {
        logger.info("Clearing " + cacheType().name() + " cache...");
        cache.clear();
        if (warmCache) {
            cache.putAll(initialEntries());
        }
    }
}
