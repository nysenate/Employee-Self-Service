package gov.nysenate.ess.core.dao.unit;

import com.google.common.collect.ImmutableMap;
import gov.nysenate.ess.core.model.unit.Location;
import gov.nysenate.ess.core.model.unit.LocationId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class LocationDao {
    private final SqlLocationDao sqlLocationDao;
    private ImmutableMap<LocationId, Location> locMap;

    @Autowired
    public LocationDao(SqlLocationDao sqlLocationDao) {
        this.sqlLocationDao = sqlLocationDao;
        // This unfortunately takes ~10 seconds, but we need it done before ESS starts.
        initializeData();
    }

    /**
     * Retrieves the location with the provided {@link LocationId}.
     * @return The location for the given {@link LocationId} or {@code null} if no location can be found with that LocationId.
     */
    public Location getLocation(LocationId locId) {
        return locMap.get(locId);
    }

    /**
     * Retrieves all Locations. Results can be limited to only active
     * locations by setting {@code activeOnly} to {@code true}
     *
     * @param activeOnly if true will only return active Locations.
     */
    public List<Location> getLocations(boolean activeOnly) {
        return locMap.values().stream().filter(loc -> !activeOnly || loc.isActive())
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

    @Scheduled(cron = "${cache.cron.location:0 0 0 * * *}")
    private void initializeData() {
        locMap = sqlLocationDao.getLocations(false).stream()
                .collect(ImmutableMap.toImmutableMap(Location::getLocId, Function.identity()));
    }
}
