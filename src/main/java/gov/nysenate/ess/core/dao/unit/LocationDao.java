package gov.nysenate.ess.core.dao.unit;

import gov.nysenate.ess.core.model.cache.CacheType;
import gov.nysenate.ess.core.model.unit.Location;
import gov.nysenate.ess.core.model.unit.LocationId;
import gov.nysenate.ess.core.service.cache.CachingService;
import gov.nysenate.ess.core.service.cache.UnclearableCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class LocationDao extends UnclearableCache<LocationId, Location> {
    private final SqlLocationDao sqlLocationDao;

    @Autowired
    public LocationDao(SqlLocationDao sqlLocationDao) {
        this.sqlLocationDao = sqlLocationDao;
    }

    /**
     * Retrieves the location with the provided {@link LocationId}.
     * @return The location for the given {@link LocationId} or {@code null} if no location can be found with that LocationId.
     */
    public synchronized Location getLocation(LocationId locId) {
        return cache.get(locId);
    }

    /**
     * Retrieves all Locations. Results can be limited to only active
     * locations by setting {@code activeOnly} to {@code true}
     *
     * @param activeOnly if true will only return active Location's.
     */
    public synchronized List<Location> getLocations(boolean activeOnly) {
        List<Location> list = new ArrayList<>();
        cache.iterator().forEachRemaining(entry -> list.add(entry.getValue()));
        if (activeOnly) {
            return list.stream().filter(Location::isActive).collect(Collectors.toList());
        }
        return list;
    }

    /**
     * Searches for locations where code matches {@code term}.
     * @param term
     * @return A list of matching locations
     */
    public List<Location> searchLocations(String term) {
        return sqlLocationDao.searchLocations(term);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CacheType cacheType() {
        return CacheType.LOCATION;
    }

    @Override
    protected Map<LocationId, Location> initialEntries() {
        return sqlLocationDao.getLocations(false).stream()
                .collect(Collectors.toMap(Location::getLocId, Function.identity()));
    }

    @Scheduled(cron = "${cache.cron.location:0 0 0 * * *}")
    private void cacheLocations() {
        clearCache(true);
    }
}
