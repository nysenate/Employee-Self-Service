package gov.nysenate.ess.core.controller.api;

import com.google.common.eventbus.EventBus;
import gov.nysenate.ess.core.client.response.base.BaseResponse;
import gov.nysenate.ess.core.client.response.base.ListViewResponse;
import gov.nysenate.ess.core.client.response.base.SimpleResponse;
import gov.nysenate.ess.core.client.view.CacheStatsView;
import gov.nysenate.ess.core.model.cache.CacheEvictEvent;
import gov.nysenate.ess.core.model.cache.CacheWarmEvent;
import gov.nysenate.ess.core.model.cache.ContentCache;
import gov.nysenate.ess.core.service.cache.EhCacheManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Collectors;

import static gov.nysenate.ess.core.controller.api.BaseRestApiCtrl.REST_PATH;

@RestController
@RequestMapping(value = REST_PATH + "admin/cache")
public class CacheManagementCtrl extends BaseRestApiCtrl {

    @Autowired EhCacheManageService cacheManageService;

    @Autowired EventBus eventBus;

    /**
     * Cache Stats Api
     * ---------------
     * Returns statistics for all ess caches
     *
     * Usage:
     * (GET)    /api/v1/admin/cache/stats
     *
     * @return ListViewResponse<CacheStatsView>
     * @see CacheStatsView for response format
     */
    @RequestMapping(value = "/stats", method = RequestMethod.GET)
    public ListViewResponse<CacheStatsView> getCacheStats() {
        return ListViewResponse.of(
                cacheManageService.getCacheStatistics().stream()
                        .map(CacheStatsView::new)
                        .collect(Collectors.toList())
        );
    }

    /**
     * Cache Warm Api
     * --------------
     * Warms one or all ess caches
     * This will clear the affected caches and populate them with default elements
     * Some caches do not support warming
     *
     * Usage:
     * (PUT)    /api/v1/admin/cache/{cacheName}
     *
     * Path Variables:
     * @param cacheName String - the name of the cache to warm, use "all" to warm all caches
     *                  @see ContentCache for cache names
     *
     * @return SimpleResponse - indicating cache warm success
     */
    @RequestMapping(value = "/{cacheName}", method = RequestMethod.PUT)
    public SimpleResponse warmCache(@PathVariable String cacheName) {
        eventBus.post(new CacheWarmEvent(getAffectedCaches(cacheName)));
        return new SimpleResponse(true, "warmed cache: " + cacheName, "cache-warm-success");
    }

    /**
     * Cache Evict Api
     * --------------
     * Evicts one or all ess caches
     *
     * Usage:
     * (DELETE)    /api/v1/admin/cache/{cacheName}
     *
     * Path Variables:
     * @param cacheName String - the name of the cache to warm, use "all" to warm all caches
     *                  @see ContentCache for cache names
     *
     * @return SimpleResponse - indicating cache warm success
     */
    @RequestMapping(value = "/{cacheName}", method = RequestMethod.DELETE)
    public SimpleResponse evictCache(@PathVariable String cacheName) {
        eventBus.post(new CacheEvictEvent(getAffectedCaches(cacheName)));
        return new SimpleResponse(true, "evicted cache: " + cacheName, "cache-evict-success");
    }

    /** --- Internal Methods --- */

    private Set<ContentCache> getAffectedCaches(String cacheName) {
        if ("all".equalsIgnoreCase(cacheName)) {
            return ContentCache.getAllContentCaches();
        }
        return EnumSet.of(
                getEnumParameter("cacheName", cacheName, ContentCache.class)
        );
    }
}
