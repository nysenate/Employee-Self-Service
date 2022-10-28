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
    @Autowired
    protected EventBus eventBus;
    // Cache creation is left up to implementations.
    protected Cache<Key, Value> cache;

    /**
     * @return Specifies which cache this is.
     */
    public abstract CacheType cacheType();

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
    public abstract void clearCache(boolean warmCache);
}
