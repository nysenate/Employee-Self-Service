package gov.nysenate.ess.core.service.cache;

import gov.nysenate.ess.core.service.personnel.ActiveEmployeeIdService;
import gov.nysenate.ess.core.util.AsyncRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.lang.reflect.ParameterizedType;
import java.util.Set;

/**
 * Contains common code for caches that map from employee IDs to some data object.
 * @param <Value>
 */
public abstract class EmployeeCache<Value> extends CachingService<Integer, Value> {
    private static final Logger logger = LoggerFactory.getLogger(EmployeeCache.class);
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
        Set<Integer> empIds;
        // Ensures we only regenerate this data once.
        synchronized (logger) {
            empIds = empIdService.getActiveEmployeeIds();
        }
        this.cache = EssCacheManager.createCache(Integer.class, valueClass, this, empIds.size());
        if (warmOnStartup) {
            asyncRunner.run(() -> empIds.forEach(this::putId));
        }
    }

    @Override
    public void evictContent(String key) {
        cache.remove(Integer.parseInt(key));
    }

    @Override
    public void clearCache(boolean warmCache) {
        logger.info("Clearing " + cacheType().name() + " cache...");
        cache.clear();
        if (warmCache) {
            asyncRunner.run(() -> empIdService.getActiveEmployeeIds().forEach(this::putId));
        }
        logger.info("Done clearing cache.");
    }

    /**
     * Puts the data associated with this employee id into the cache.
     */
    protected abstract void putId(int id);
}
