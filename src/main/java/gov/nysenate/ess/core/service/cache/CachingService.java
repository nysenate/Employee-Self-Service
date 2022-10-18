package gov.nysenate.ess.core.service.cache;

import com.google.common.eventbus.EventBus;
import gov.nysenate.ess.core.model.cache.CacheType;
import org.ehcache.Cache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.Map;

/**
 * Defines methods that are required to manage a service that depends on one or more caches.
 */
public abstract class CachingService<Key, Value> {
    private static final int FOR_ROUNDING = 50;
    private static final Logger logger = LoggerFactory.getLogger(CachingService.class);
    @Autowired
    protected EventBus eventBus;
    protected Cache<Key, Value> cache;

    @PostConstruct
    private void init() {
        Map<Key, Value> initialEntries = initialEntries();
        var size = (Math.floor(initialEntries.size() * 1.1/FOR_ROUNDING) + 1) * FOR_ROUNDING;
        this.cache = EssCacheManager.createCache(this, (int) size);
        initialEntries.forEach((k, v) -> cache.put(k, v));
    }

    /**
     * @return Specifies which cache this is.
     */
    public abstract CacheType cacheType();

    protected Map<Key, Value> initialEntries() {
        return Map.of();
    }

    @SuppressWarnings("unchecked")
    protected void evictContent(String key) throws ClassCastException {
        Key typedKey;
        try {
            typedKey = (Key) key;
        }
        catch (ClassCastException ex) {
            typedKey = (Key) (Integer) Integer.parseInt(key);
        }
        cache.remove(typedKey);
    }

    /**
     * Clears all the cache entries from cache.
     */
    public synchronized void clearCache(boolean warmCache) {
        logger.info("Clearing " + cacheType().name() + " cache...");
        cache.clear();
        if (warmCache) {
            cache.putAll(initialEntries());
        }
        logger.info("Done clearing cache.");
    }
}
