package gov.nysenate.ess.core.service.cache;

import gov.nysenate.ess.core.model.cache.CacheType;
import gov.nysenate.ess.core.service.base.CachingService;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.core.internal.statistics.DefaultStatisticsService;
import org.ehcache.core.spi.service.StatisticsService;
import org.ehcache.core.statistics.CacheStatistics;
import org.ehcache.expiry.ExpiryPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.ParameterizedType;
import java.time.Duration;
import java.util.EnumMap;
import java.util.Set;

public final class EssCacheManager {
    private static final Logger logger = LoggerFactory.getLogger(EssCacheManager.class);
    private static final StatisticsService statisticsService = new DefaultStatisticsService();
    private static final CacheManager cacheManager = CacheManagerBuilder.newCacheManagerBuilder()
            .using(statisticsService).build(true);
    private static final EnumMap<CacheType, CachingService<?, ?>> cacheTypeMap =
            new EnumMap<>(CacheType.class);
    private static final EnumMap<CacheType, Integer> cacheCapacityMap = new EnumMap<>(CacheType.class);

    @SuppressWarnings("unchecked")
    public static <K, V> Cache<K, V> createCache(CachingService<K, V> service, int size) {
        var genSuper = service.getClass().getGenericSuperclass();
        var classes = ((ParameterizedType) genSuper)
                .getActualTypeArguments();
        var keyClass = (Class<K>) classes[0];
        Class<V> valueClass;
        try {
            valueClass = (Class<V>) classes[1];
        }
        catch (ClassCastException ex) {
            valueClass = (Class<V>) ((ParameterizedType) classes[1]).getRawType();
        }
        var type = service.cacheType();

        cacheCapacityMap.put(type, size);
        var expiryPolicy = service.expiryTimeSeconds() <= 0 ? ExpiryPolicy.NO_EXPIRY :
                ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofSeconds(service.expiryTimeSeconds()));
        var config = CacheConfigurationBuilder
                .newCacheConfigurationBuilder(keyClass, valueClass, ResourcePoolsBuilder.heap(size))
                .withSizeOfMaxObjectGraph(100000).withExpiry(expiryPolicy);
        cacheTypeMap.put(type, service);
        return cacheManager.createCache(type.name(), config);
    }

    public static CacheStatistics getCacheStats(CacheType type) {
        return statisticsService.getCacheStatistics(type.name());
    }

    public static int getCapacity(CacheType type) {
        return cacheCapacityMap.get(type);
    }

    public static synchronized void clearCaches(Set<CacheType> types, boolean warmCaches) {
        for (var cachingService : types.stream().map(cacheTypeMap::get).toList()) {
            cachingService.clearCache(warmCaches);
        }
    }
}
