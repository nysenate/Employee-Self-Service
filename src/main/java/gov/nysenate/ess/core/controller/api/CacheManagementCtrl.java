package gov.nysenate.ess.core.controller.api;

import com.google.common.eventbus.EventBus;
import gov.nysenate.ess.core.client.response.base.ListViewResponse;
import gov.nysenate.ess.core.client.response.base.SimpleResponse;
import gov.nysenate.ess.core.client.view.CacheStatsView;
import gov.nysenate.ess.core.model.cache.CacheType;
import gov.nysenate.ess.core.service.cache.EssCacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;

import static gov.nysenate.ess.core.controller.api.BaseRestApiCtrl.ADMIN_REST_PATH;
import static gov.nysenate.ess.core.model.auth.SimpleEssPermission.ADMIN;

@RestController
@RequestMapping(value = ADMIN_REST_PATH + "/cache")
public class CacheManagementCtrl extends BaseRestApiCtrl {
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
    @RequestMapping(value = "/stats", method = {RequestMethod.GET, RequestMethod.HEAD})
    public ListViewResponse<CacheStatsView> getCacheStats() {
        checkPermission(ADMIN.getPermission());
        return ListViewResponse.of(Arrays.stream(CacheType.values())
                .map(EssCacheManager::getStatsView).toList());
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
     *                  @see CacheType for cache names
     *
     * @return SimpleResponse - indicating cache warm success
     */
    @RequestMapping(value = "/{cacheName}", method = RequestMethod.PUT)
    public SimpleResponse warmCache(@PathVariable String cacheName) {
        checkPermission(ADMIN.getPermission());
        EssCacheManager.clearCaches(getAffectedCaches(cacheName), true);
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
     * @param cacheName String - the name of the cache to evict, use "all" to evict all caches
     *                  @see CacheType for cache names
     *
     * @return SimpleResponse - indicating cache evict success
     */
    @RequestMapping(value = "/{cacheName}", method = RequestMethod.DELETE)
    public SimpleResponse evictCache(@PathVariable String cacheName) {
        checkPermission(ADMIN.getPermission());
        EssCacheManager.clearCaches(getAffectedCaches(cacheName), false);
        return new SimpleResponse(true, "evicted cache: " + cacheName, "cache-evict-success");
    }

    /**
     * Cache Element Evict Api
     * --------------
     * Evicts a single element from an ess cache
     *
     * Usage:
     * (DELETE)    /api/v1/admin/cache/{cacheName}
     *
     * Path Variables:
     * @param cacheName String - the name of the cache to warm, use "all" to warm all caches
     *                  @see CacheType for cache names
     * Request Params:
     * @param key String - key for the element to evict
     *
     * @return SimpleResponse - indicating cache element evict success
     */
    @RequestMapping(value = "/{cacheName}", params = {"key"}, method = RequestMethod.DELETE)
    public SimpleResponse evictCacheElement(@PathVariable String cacheName,
                                            @RequestParam String key) {
        checkPermission(ADMIN.getPermission());
        CacheType type = getEnumParameter("cacheName", cacheName, CacheType.class);
        EssCacheManager.removeEntry(type, key);
        return new SimpleResponse(true,
                "evicted element:" + key + " from cache:" + cacheName,
                "cache-evict-success");
    }

    /** --- Internal Methods --- */

    private Set<CacheType> getAffectedCaches(String cacheName) {
        if ("all".equalsIgnoreCase(cacheName)) {
            return Set.of(CacheType.values());
        }
        return EnumSet.of(getEnumParameter("cacheName", cacheName, CacheType.class));
    }
}
