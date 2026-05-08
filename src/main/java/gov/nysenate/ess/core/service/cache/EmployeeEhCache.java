package gov.nysenate.ess.core.service.cache;

import gov.nysenate.ess.core.service.personnel.ActiveEmployeeIdService;
import gov.nysenate.ess.core.util.AsyncRunner;
import org.ehcache.Cache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import jakarta.annotation.PostConstruct;
import java.lang.reflect.ParameterizedType;
import java.util.Set;

/**
 * Contains common code for caches that map from employee IDs to some data object.
 *
 * @param <ValueType>
 */
public abstract class EmployeeEhCache<ValueType> extends CachingService {
    private static final Logger logger = LoggerFactory.getLogger(EmployeeEhCache.class);

    protected Cache<Integer, ValueType> cache;
    @Autowired
    private AsyncRunner asyncRunner;
    @Autowired
    private ActiveEmployeeIdService empIdService;
    @Value("${cache.cron.employee:0 0 0 * * *}")
    private String empCron;
    @Value("${cache.warm.on.startup:true}")
    private boolean warmOnStartup;

    @SuppressWarnings("unchecked")
    @PostConstruct
    private void init() {
        Class<ValueType> valueClass = (Class<ValueType>) ((ParameterizedType) getClass()
                .getGenericSuperclass()).getActualTypeArguments()[0];
        Set<Integer> empIds = empIdService.getActiveEmployeeIds();
        this.cache = EssCacheManager.createCache(Integer.class, valueClass, this, empIds.size());
        if (warmOnStartup) {
            asyncRunner.run(() -> {
                logger.info("Starting warming {} cache", cacheType());
                warmCache();
                logger.info("Finished warming {} cache", cacheType());
            });
        }
    }

    @Override
    public void evictContent(String key) {
        cache.remove(Integer.parseInt(key));
    }

    @Override
    public void clearCache(boolean warmCache) {
        logger.info("Clearing {} cache...", cacheType().name());
        cache.clear();
        if (warmCache) {
            asyncRunner.run(this::warmCache);
        }
        logger.info("Done clearing cache.");
    }

    @Override
    protected String getCron() {
        return empCron;
    }

    protected abstract void warmCache();
}
