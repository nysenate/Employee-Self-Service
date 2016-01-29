package gov.nysenate.ess.core.service.unit;

import com.google.common.eventbus.EventBus;
import gov.nysenate.ess.core.dao.unit.LocationDao;
import gov.nysenate.ess.core.model.cache.ContentCache;
import gov.nysenate.ess.core.model.unit.Location;
import gov.nysenate.ess.core.model.unit.LocationType;
import gov.nysenate.ess.core.service.cache.EhCacheManageService;
import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

@Service
public class EssCachedLocationService implements LocationService {

    private static final Logger logger = LoggerFactory.getLogger(EssCachedLocationService.class);
    private static final String LOCATION_CACHE_KEY = "location";

    @Autowired private LocationDao locationDao;
    @Autowired private EventBus eventBus;
    @Autowired private EhCacheManageService cacheManageService;
    private Cache locationCache;

    private static final class LocationCacheTree {

        private TreeMap<String, Location> locationTreeMap = new TreeMap<>();

        public LocationCacheTree(List<Location> locations) {
            locations.forEach(loc -> locationTreeMap.put(getLocationKey(loc), loc));
        }

        public Location getLocation(String code, LocationType type) {
            return locationTreeMap.get(code + "-" + String.valueOf(type.getName()));
        }

        public List<Location> getLocations() {
            return new ArrayList<>(locationTreeMap.values());
        }

        /** Return the unique key for a location. Made up of location code and type. */
        private String getLocationKey(Location loc) {
            return loc.getCode() + "-" + String.valueOf(loc.getType().getCode());
        }
    }

    @PostConstruct
    public void init() {
        this.eventBus.register(this);
        this.locationCache = this.cacheManageService.registerEternalCache(ContentCache.LOCATION.name());
        if (this.cacheManageService.isWarmOnStartup()) {
            cacheLocations();
        }
    }

    @Override
    public Location getLocation(String locCode, LocationType locType) {
        return getLocationCacheTree().getLocation(locCode, locType);
    }

    @Override
    public List<Location> getLocations() {
        return getLocationCacheTree().getLocations();
    }

    private LocationCacheTree getLocationCacheTree() {
        Element element = getCachedLocationMap();
        if (element == null) {
            cacheLocations();
            element = getCachedLocationMap();
        }
        return (LocationCacheTree) element.getObjectValue();
    }

    private Element getCachedLocationMap() {
        Element element;
        locationCache.acquireReadLockOnKey(LOCATION_CACHE_KEY);
        try {
            element = locationCache.get(LOCATION_CACHE_KEY);
        }
        finally {
            locationCache.releaseReadLockOnKey(LOCATION_CACHE_KEY);
        }
        return element;
    }

    @Scheduled(cron = "${cache.cron.location}")
    private void cacheLocations() {
        logger.info("Caching Locations...");
        locationCache.acquireWriteLockOnKey(LOCATION_CACHE_KEY);
        try {
            locationCache.remove(LOCATION_CACHE_KEY);
            locationCache.put(new Element(LOCATION_CACHE_KEY, new LocationCacheTree(locationDao.getLocations())));
        }
        finally {
            locationCache.releaseWriteLockOnKey(LOCATION_CACHE_KEY);
        }
        logger.info("Done caching locations.");
    }

}
