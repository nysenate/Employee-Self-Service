package gov.nysenate.ess.core.service.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class UnclearableCache<K, V> extends CachingService<K, V> {
    private static final Logger logger = LoggerFactory.getLogger(UnclearableCache.class);
    private final String warningMessage = cacheType() + " can't be cleared unless it's also being warmed.";

    @Override
    public synchronized void clearCache(boolean warmCache) {
        if (!warmCache) {
            logger.warn(warningMessage);
            throw new UnsupportedOperationException(warningMessage);
        }
        else {
            super.clearCache(true);
        }
    }
}
