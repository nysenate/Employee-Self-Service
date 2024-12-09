package gov.nysenate.ess.core.service.cache;

import gov.nysenate.ess.core.service.personnel.ActiveEmployeeIdService;
import gov.nysenate.ess.core.util.AsyncRunner;
import org.ehcache.Cache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.lang.reflect.ParameterizedType;
import java.util.Set;

/**
 * Contains common code for caches that map from employee IDs to some data object.
 *
 * @param <Value>
 */
@Service
public abstract class EmployeeEhCache<Value> extends CachingService {
    private static final Logger logger = LoggerFactory.getLogger(EmployeeEhCache.class);

    protected Cache<Integer, Value> cache;
    @Autowired
    private AsyncRunner asyncRunner;
    @Autowired
    private ActiveEmployeeIdService empIdService;
    @org.springframework.beans.factory.annotation.Value("${cache.warm.on.startup:true}")
    private boolean warmOnStartup;

    @SuppressWarnings("unchecked")
    @PostConstruct
    private void init() {
        Class<Value> valueClass = (Class<Value>) ((ParameterizedType) getClass()
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

    protected abstract void warmCache();
}
