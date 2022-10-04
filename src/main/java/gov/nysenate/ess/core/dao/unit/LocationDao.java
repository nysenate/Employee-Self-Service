package gov.nysenate.ess.core.dao.unit;

import gov.nysenate.ess.core.model.cache.CacheType;
import gov.nysenate.ess.core.model.unit.Location;
import gov.nysenate.ess.core.model.unit.LocationId;
import gov.nysenate.ess.core.service.base.CachingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class LocationDao extends CachingService<String, LocationDao.LocationCacheTree> {
    private static final Logger logger = LoggerFactory.getLogger(LocationDao.class);
    private static final String LOCATION_CACHE_KEY = "location";
    private final SqlLocationDao sqlLocationDao;

    @Autowired
    public LocationDao(SqlLocationDao sqlLocationDao) {
        this.sqlLocationDao = sqlLocationDao;
        if (warmOnStartup) {
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
        return getLocations(false);
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
     * Searches for locations where code matches {@code term}.
     * @param term
     * @return A list of matching locations
     */
    public List<Location> searchLocations(String term) {
        return sqlLocationDao.searchLocations(term);
    }

    static final class LocationCacheTree {
        private final HashMap<LocationId, Location> locationTreeMap = new HashMap<>();

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

    /**
     * {@inheritDoc}
     */
    @Override
    public CacheType cacheType() {
        return CacheType.LOCATION;
    }

    /**
     * --- Internal Methods ---
     */

    private LocationCacheTree getLocationCacheTree() {
        LocationCacheTree tree = cache.get(LOCATION_CACHE_KEY);;
        if (tree == null || tree.getLocations().isEmpty()) {
            cacheLocations();
            tree = cache.get(LOCATION_CACHE_KEY);;
        }
        return tree;
    }

    @Override
    protected Map<String, LocationCacheTree> initialEntries() {
        return Map.of(LOCATION_CACHE_KEY, new LocationCacheTree(sqlLocationDao.getLocations(false)));
    }

    @Scheduled(cron = "${cache.cron.location:0 0 0 * * *}")
    private void cacheLocations() {
        clearCache(true);
    }
}
