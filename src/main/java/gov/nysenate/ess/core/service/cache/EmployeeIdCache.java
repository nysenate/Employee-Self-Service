package gov.nysenate.ess.core.service.cache;

import gov.nysenate.ess.core.dao.personnel.EmployeeDao;
import gov.nysenate.ess.core.util.AsyncRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.lang.reflect.ParameterizedType;
import java.util.TreeSet;

public abstract class EmployeeIdCache<Value> extends CachingService<Integer, Value> {
    private static final Logger logger = LoggerFactory.getLogger(EmployeeIdCache.class);
    private static TreeSet<Integer> empIds = null;
    @Autowired
    private AsyncRunner asyncRunner;
    @Autowired
    private EmployeeDao employeeDao;

    @SuppressWarnings("unchecked")
    @PostConstruct
    private synchronized void init() {
        if (empIds == null) {
            empIds = new TreeSet<>(employeeDao.getActiveEmployeeIds());
        }
        Class<Value> valueClass = (Class<Value>) ((ParameterizedType) getClass()
                .getGenericSuperclass()).getActualTypeArguments()[0];
        this.cache = EssCacheManager.createCache(Integer.class, valueClass, this, empIds.size());
        asyncRunner.run(() -> empIds.forEach(this::putId));
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
            employeeDao.getActiveEmployeeIds().forEach(this::putId);
        }
        logger.info("Done clearing cache.");
    }

    /**
     * Puts the data associated with this employee id into the cache.
     */
    protected abstract void putId(int id);
}
