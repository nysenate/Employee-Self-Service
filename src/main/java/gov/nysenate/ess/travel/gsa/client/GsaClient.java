package gov.nysenate.ess.travel.gsa.client;

import com.google.common.eventbus.EventBus;
import gov.nysenate.ess.core.model.cache.ContentCache;
import gov.nysenate.ess.core.service.base.CachingService;
import gov.nysenate.ess.core.service.cache.EhCacheManageService;
import gov.nysenate.ess.core.util.HttpUtils;
import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.Month;

/**
 * The GsaClient handles retrieving GSA data from a given date and zip code.
 * Caches responses from the GSA API to improve response times and reduce API calls.
 *
 * Uses the {@link GsaResponseParser} to parse the raw json GSA API response into
 * a {@link GsaResponse} object.
 *
 * GSA API Docs: https://www.gsa.gov/technology/government-it-initiatives/digital-strategy/per-diem-apis/api-for-per-diem-rates
 */
@Component
public class GsaClient implements CachingService<GsaResponseId> {

    private static final Logger logger = LoggerFactory.getLogger(GsaClient.class);
    private final long cacheSize = 1L; // Cache size in MB.

    private String baseUrl;
    private GsaResponseParser gsaResponseParser;
    private EventBus eventBus;
    private EhCacheManageService cacheManageService;
    private volatile Cache gsaCache;

    @Autowired
    public GsaClient(@Value("${travel.gsa.api.url}") String baseUrl, GsaResponseParser gsaResponseParser,
                     EventBus eventBus, EhCacheManageService cacheManageService) {
        this.baseUrl = baseUrl;
        this.gsaResponseParser = gsaResponseParser;
        this.eventBus = eventBus;
        this.cacheManageService = cacheManageService;

        this.eventBus.register(this);
        this.gsaCache = this.cacheManageService.registerMemoryBasedCache(getCacheType().name(), cacheSize);
    }

    /**
     * Returns data from the GSA API in a {@link GsaResponse} object for the given date and zip.
     * Will return GsaResponse from the cache if it exists, otherwise will query the GSA API
     * and cache the response.
     *
     * @param date The date to get GSA rates for.
     * @param zip The zip code to get GSA rates for.
     * @return
     * @throws IOException
     */
    public GsaResponse queryGsa(LocalDate date, String zip) throws IOException {
        GsaResponseId id = new GsaResponseId(getFiscalYear(date), zip);
        GsaResponse response = queryCache(id);
        if (response == null) {
            response = queryApi(id);
            saveToCache(response);
        }
        return response;
    }

    private GsaResponse queryApi(GsaResponseId id) throws IOException {
        // Example query: {"FiscalYear":"2018","Zip":"12208"}
        String query = "{\"FiscalYear\":\"" + String.valueOf(id.getFiscalYear())
                + "\",\"Zip\":\"" + id.getZipcode() + "\"}";
        String url = baseUrl + URLEncoder.encode(query, "UTF-8");
        String content = HttpUtils.urlToString(url);
        return gsaResponseParser.parseGsaResponse(content);
    }

    private GsaResponse queryCache(GsaResponseId gsaId) {
        gsaCache.acquireReadLockOnKey(gsaId);
        Element e = gsaCache.get(gsaId);
        gsaCache.releaseReadLockOnKey(gsaId);
        return e == null ? null : (GsaResponse) e.getObjectValue();
    }

    private void saveToCache(GsaResponse response) {
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

    private int getFiscalYear(LocalDate date) {
        int year = date.getYear();
        int month = date.getMonthValue();

        int fiscalYear = year;
        if (month >= Month.OCTOBER.getValue()) {
            fiscalYear++;
        }
        return fiscalYear;
    }
}