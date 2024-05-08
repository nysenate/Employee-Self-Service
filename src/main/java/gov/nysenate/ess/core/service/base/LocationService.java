package gov.nysenate.ess.core.service.base;

import gov.nysenate.ess.core.dao.unit.SqlLocationDao;
import gov.nysenate.ess.core.model.cache.CacheType;
import gov.nysenate.ess.core.model.unit.Location;
import gov.nysenate.ess.core.model.unit.LocationId;
import gov.nysenate.ess.core.service.RefreshedCachedData;
import gov.nysenate.ess.core.util.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;
import java.util.stream.Collectors;

public class LocationService extends RefreshedCachedData<LocationId, Location> {
    private final SqlLocationDao locationDao;

    @Autowired
    public LocationService(SqlLocationDao sqlLocationDao, @Value("${cache.cron.location}") String cron) {
        super(cron, () -> CollectionUtils.valuesToMap(sqlLocationDao.getAllLocations(), Location::getLocId));
        this.locationDao = sqlLocationDao;
    }

    /**
     * Retrieves the location with the provided {@link LocationId}.
     * @return The location for the given {@link LocationId} or {@code null} if no location can be found with that LocationId.
     */
    public Location getLocation(LocationId locId) {
        return dataMap().get(locId);
    }

    /**
     * Retrieves all Locations. Results can be limited to only active
     * locations by setting {@code activeOnly} to {@code true}
     *
     * @param activeOnly if true will only return active Locations.
     */
    public List<Location> getLocations(boolean activeOnly) {
        return dataMap().values().stream().filter(loc -> !activeOnly || loc.isActive())
                .collect(Collectors.toList());
    }

    /**
     * Searches for locations where code matches {@code term}.
     * @return A list of matching locations
     */
    public List<Location> searchLocations(String term) {
        return locationDao.searchLocations(term);
    }

    @Override
    public CacheType cacheType() {
        return CacheType.LOCATION;
    }
}
