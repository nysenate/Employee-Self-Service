package gov.nysenate.ess.travel.provider.gsa;

import com.google.common.eventbus.EventBus;
import gov.nysenate.ess.core.model.cache.ContentCache;
import gov.nysenate.ess.core.service.base.CachingService;
import gov.nysenate.ess.core.service.cache.EhCacheManageService;
import gov.nysenate.ess.core.util.DateUtils;
import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

/**
 * Responsible for a small cache of GsaResponse objects.
 * Each GsaResponse object is uniquely identified by its GsaResponseId.
 */
@Service
public class GsaCache implements CachingService<GsaResponseId> {

    private static final Logger logger = LoggerFactory.getLogger(GsaCache.class);
    private final long cacheSize = 2L; // Cache size in MB.

    private EventBus eventBus;
    private EhCacheManageService cacheManageService;
    private volatile Cache gsaCache;

    @Autowired
    public GsaCache(EventBus eventBus, EhCacheManageService cacheManageService) {
        this.eventBus = eventBus;
        this.cacheManageService = cacheManageService;

        this.eventBus.register(this);
        this.gsaCache = this.cacheManageService.registerMemoryBasedCache(getCacheType().name(), cacheSize);
    }

    /**
     * Returns a {@link GsaResponse} from the cache if it exists, otherwise returns null.
     *
     * @param date The date to get GSA rates for.
     * @param zip The zip code to get GSA rates for.
     * @return
     */
    protected GsaResponse queryGsa(LocalDate date, String zip) {
        GsaResponseId id = new GsaResponseId(DateUtils.getFederalFiscalYear(date), zip);
        return queryCache(id);
    }

    private GsaResponse queryCache(GsaResponseId gsaId) {
        gsaCache.acquireReadLockOnKey(gsaId);
        Element e = gsaCache.get(gsaId);
        gsaCache.releaseReadLockOnKey(gsaId);
        return e == null ? null : (GsaResponse) e.getObjectValue();
    }

    protected void saveToCache(GsaResponse response) {
        GsaResponseId id = response.getId();
        gsaCache.acquireWriteLockOnKey(id);
        gsaCache.put(new Element(id, response));
        gsaCache.releaseWriteLockOnKey(id);
    }

    @Override
    public ContentCache getCacheType() {
        return ContentCache.GSA_API;
    }

    @Override
    public void evictContent(GsaResponseId gsaResponseId) {
        gsaCache.remove(gsaResponseId);
    }

    @Override
    public void evictCache() {
        logger.info("Clearing {} cache.", getCacheType());
        gsaCache.removeAll();
    }
}