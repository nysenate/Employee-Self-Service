package gov.nysenate.ess.core.dao.unit;

import com.google.common.eventbus.EventBus;
import gov.nysenate.ess.core.model.cache.ContentCache;
import gov.nysenate.ess.core.model.personnel.ResponsibilityHead;
import gov.nysenate.ess.core.model.unit.Location;
import gov.nysenate.ess.core.model.unit.LocationId;
import gov.nysenate.ess.core.service.base.CachingService;
import gov.nysenate.ess.core.service.cache.EhCacheManageService;
import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LocationDao implements CachingService<String> {

    private static final Logger logger = LoggerFactory.getLogger(LocationDao.class);
    private static final String LOCATION_CACHE_KEY = "location";

    private SqlLocationDao sqlLocationDao;
    private EventBus eventBus;
    private EhCacheManageService cacheManageService;
    private volatile Cache locationCache;

    public LocationDao(SqlLocationDao sqlLocationDao, EventBus eventBus, EhCacheManageService cacheManageService) {
        this.sqlLocationDao = sqlLocationDao;
        this.eventBus = eventBus;
        this.cacheManageService = cacheManageService;

        this.eventBus.register(this);
        this.locationCache = this.cacheManageService.registerEternalCache(getCacheType().name());
        if (this.cacheManageService.isWarmOnStartup()) {
            cacheLocations();
        }
    }

    /**
     * Retrieves the location with the provided {@link LocationId}.
     * @return The location for the given {@link LocationId} or {@code null} if no location can be found with that LocationId.
     */
    public Location getLocation(LocationId locId) {
        return getLocationCacheTree().getLocation(locId);
    }

    /**
     * Retrieves all Locations including inactive.
     * @return
     */
    public List<Location> getLocations() {
        return this.getLocations(false);
    }

    /**
     * Retrieves all Locations. Results can be limited to only active
     * locations by setting {@code activeOnly} to {@code true}
     *
     * @param activeOnly if true will only return active Location's.
     */
    public List<Location> getLocations(boolean activeOnly) {
        if (activeOnly) {
            return getLocationCacheTree().getLocations().stream()
                    .filter(Location::isActive)
                    .collect(Collectors.toList());
        } else {
            return getLocationCacheTree().getLocations();
        }
    }

    /**
     * Get active locations that are managed by the given {@code responsibilityHead}.
     * @return A list of matching locations or an empty list if no matches were found.
     */
    public List<Location> getLocationsByResponsibilityHead(ResponsibilityHead responsibilityHead) {
        return getLocations(true).stream()
                .filter(loc -> loc.getResponsibilityHead().equals(responsibilityHead))
                .collect(Collectors.toList());
    }

    /**
     * Searches for locations where code matches {@code term}.
     * @param term
     * @return A list of matching locations
     */
    public List<Location> searchLocations(String term) {
        return sqlLocationDao.searchLocations(term);
    }

    private static final class LocationCacheTree {

        private HashMap<LocationId, Location> locationTreeMap = new HashMap<>();

        public LocationCacheTree(List<Location> locations) {
            for (Location loc : locations) {
                locationTreeMap.put(loc.getLocId(), loc);
            }
        }

        public Location getLocation(LocationId locId) {
            return locationTreeMap.get(locId);
        }

        public List<Location> getLocations() {
            return new ArrayList<>(locationTreeMap.values());
        }
    }

    /** --- Caching Service Implemented Methods --- */

    /**
     * {@inheritDoc}
     */
    @Override
    public ContentCache getCacheType() {
        return ContentCache.LOCATION;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void evictContent(String key) {
        locationCache.remove(key);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void evictCache() {
        logger.info("Clearing {} cache..", getCacheType());
        locationCache.removeAll();
    }

    /**
     * --- Internal Methods ---
     */

    private LocationCacheTree getLocationCacheTree() {
        Element element = getCachedLocationMap();
        if (cacheIsEmpty(element)) {
            cacheLocations();
            element = getCachedLocationMap();
        }
        return (LocationCacheTree) element.getObjectValue();
    }

    private boolean cacheIsEmpty(Element element) {
        if (element == null) {
            return true;
        }
        LocationCacheTree locationCacheTree = (LocationCacheTree) element.getObjectValue();
        return locationCacheTree.getLocations().size() == 0;
    }

    private Element getCachedLocationMap() {
        Element element;
        locationCache.acquireReadLockOnKey(LOCATION_CACHE_KEY);
        try {
            element = locationCache.get(LOCATION_CACHE_KEY);
        } finally {
            locationCache.releaseReadLockOnKey(LOCATION_CACHE_KEY);
        }
        return element;
    }

    @Scheduled(cron = "${cache.cron.location:0 0 0 * * *}")
    private void cacheLocations() {
        logger.info("Warming Locations cache...");
        locationCache.acquireWriteLockOnKey(LOCATION_CACHE_KEY);
        try {
            locationCache.remove(LOCATION_CACHE_KEY);
            locationCache.put(new Element(LOCATION_CACHE_KEY, new LocationCacheTree(sqlLocationDao.getLocations(false))));
        } finally {
            locationCache.releaseWriteLockOnKey(LOCATION_CACHE_KEY);
        }
        logger.info("Done caching locations.");
    }
}
