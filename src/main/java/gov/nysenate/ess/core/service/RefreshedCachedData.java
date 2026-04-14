package gov.nysenate.ess.core.service;

import com.google.common.collect.ImmutableMap;
import gov.nysenate.ess.core.service.cache.CachingService;
import gov.nysenate.ess.core.service.cache.EssCacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.function.Supplier;

/**
 * Some common code for classes that cache data for quicker access, where the cache is periodically refreshed.
 */
public abstract class RefreshedCachedData<K, V> extends CachingService {
    private static final Logger logger = LoggerFactory.getLogger(RefreshedCachedData.class);
    private final String cron;
    private final Supplier<Map<K, V>> mapSupplier;
    private ImmutableMap<K, V> dataMap = null;

    protected RefreshedCachedData(String cron, Supplier<Map<K, V>> mapSupplier) {
        this.cron = cron;
        this.mapSupplier = mapSupplier;
        EssCacheManager.addCachingService(this);
    }

    @Override
    public void evictContent(String key) {
        throw new UnsupportedOperationException(RefreshedCachedData.class + " must be refreshed all at once.");
    }

    @Override
    public void clearCache(boolean warmCache) {
        if (!warmCache) {
            throw new UnsupportedOperationException(RefreshedCachedData.class + " must be warmed when cleared.");
        }
        logger.info("Refreshing data for {}", cacheType());
        dataMap = ImmutableMap.copyOf(mapSupplier.get());
    }

    @Override
    protected String getCron() {
        return cron;
    }

    /**
     * Access to the internal map is only allowed through this method,
     * ensuring the map always exists, though it need not be created on start-up.
     * @return the current data.
     */
    protected Map<K, V> dataMap() {
        if (dataMap == null) {
            clearCache(true);
        }
        return dataMap;
    }
}
