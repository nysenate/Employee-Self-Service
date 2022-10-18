package gov.nysenate.ess.core.service.cache;

import gov.nysenate.ess.core.client.view.CacheStatsView;
import gov.nysenate.ess.core.model.cache.CacheType;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.core.internal.statistics.DefaultStatisticsService;
import org.ehcache.core.spi.service.StatisticsService;
import org.ehcache.core.statistics.CacheStatistics;
import org.ehcache.expiry.ExpiryPolicy;

import java.lang.reflect.ParameterizedType;
import java.util.EnumMap;
import java.util.Set;

public final class EssCacheManager {
    private static final StatisticsService statisticsService = new DefaultStatisticsService();
    private static final CacheManager cacheManager = CacheManagerBuilder.newCacheManagerBuilder()
            .using(statisticsService).build(true);
    private static final EnumMap<CacheType, CachingService<?, ?>> cacheTypeMap =
            new EnumMap<>(CacheType.class);
    private static final EnumMap<CacheType, Integer> cacheCapacityMap = new EnumMap<>(CacheType.class);

    @SuppressWarnings("unchecked")
    static <K, V> Cache<K, V> createCache(CachingService<K, V> service, int size) {
        var genSuper = service.getClass().getGenericSuperclass();
        var classes = ((ParameterizedType) genSuper)
                .getActualTypeArguments();
        var keyClass = (Class<K>) classes[0];
        Class<V> valueClass = (Class<V>) classes[1];
        var type = service.cacheType();

        cacheCapacityMap.put(type, size);
        var config = CacheConfigurationBuilder
                .newCacheConfigurationBuilder(keyClass, valueClass, ResourcePoolsBuilder.heap(size))
                .withSizeOfMaxObjectGraph(100000).withExpiry(ExpiryPolicy.NO_EXPIRY);
        cacheTypeMap.put(type, service);
        return cacheManager.createCache(type.name(), config);
    }

    public static void removeEntry(CacheType type, String key) {
        cacheTypeMap.get(type).evictContent(key);
    }

    public static CacheStatsView getStatsView(CacheType type) {
        int capacity = cacheCapacityMap.get(type);
        CacheStatistics stats = statisticsService.getCacheStatistics(type.name());
        return new CacheStatsView(type.name(),
                stats.getTierStatistics().get("OnHeap").getMappings(), capacity,
                stats.getCachePuts(), stats.getCacheRemovals(), stats.getCacheEvictions(),
                stats.getCacheExpirations(), stats.getCacheHits(), stats.getCacheMisses(),
                stats.getCacheHitPercentage());
    }

    public static synchronized void clearCaches(Set<CacheType> types, boolean warmCaches) {
        for (var cachingService : types.stream().map(cacheTypeMap::get).toList()) {
            cachingService.clearCache(warmCaches);
        }
    }
}
