package gov.nysenate.ess.core.service.base;

import com.google.common.eventbus.Subscribe;
import gov.nysenate.ess.core.model.cache.CacheEvictEvent;
import gov.nysenate.ess.core.model.cache.CacheEvictIdEvent;
import gov.nysenate.ess.core.model.cache.CacheWarmEvent;
import gov.nysenate.ess.core.model.cache.ContentCache;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.config.SizeOfPolicyConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static net.sf.ehcache.config.SizeOfPolicyConfiguration.MaxDepthExceededBehavior.ABORT;

public interface CachingService<ContentId>
{
    Logger logger = LoggerFactory.getLogger(CachingService.class);

    /**
     * Performs cache creation and any pre-caching of data.
     */
    void setupCaches();

    /**
     * @return A ContentCache enum specifying which cache this is
     */
    ContentCache getCacheType();

    /**
     * Returns all cache instances.
     */
    List<Ehcache> getCaches();

    /**
     * Evicts a single item from the cache based on the given content id
     */
    void evictContent(ContentId contentId);

    /**
     * (Default Method)
     * Clears all the cache entries from all caches.
     */
    default void evictCaches() {
        if (getCaches() != null && !getCaches().isEmpty()) {
            getCaches().forEach(c -> {
                logger.info("Clearing out {} cache", c.getName());
                c.removeAll();
            });
        }
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
            evictCaches();
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
     * Pre-fetch a subset of currently active data and store it in the cache.
     */
    void warmCaches();

    /**
     * If a CacheWarmEvent is sent out on the event bus, the caching service
     * should check to if it has any affected caches and warm them.
     *
     * @param warmEvent CacheWarmEvent
     */
    @Subscribe
    default void handleCacheWarmEvent(CacheWarmEvent warmEvent) {
        if (warmEvent.affects(getCacheType())) {
            warmCaches();
        }
    }

    /**
     * (Default Method)
     * Default 'size of' configuration which sets the maximum limit for how many nodes are traversed
     * when computing the heap size of an object before bailing out to minimize performance impact.
     *
     * @return SizeOfPolicyConfiguration
     */
    default SizeOfPolicyConfiguration defaultSizeOfPolicy() {
        return new SizeOfPolicyConfiguration().maxDepth(50000).maxDepthExceededBehavior(ABORT);
    }
}
