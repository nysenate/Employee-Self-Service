package gov.nysenate.ess.core.dao.unit;

import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import gov.nysenate.ess.core.model.unit.Location;
import gov.nysenate.ess.core.model.unit.LocationId;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SqlLocationDao extends SqlBaseDao {

    private final Logger logger = LoggerFactory.getLogger(SqlLocationDao.class);

    @Autowired private SqlLocationCountyDao locationCountyDao;

    /**
     * Retrieves the location with the provided {@link LocationId}.
     */
    Location getLocationById(LocationId locId) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("locCode", locId.getCode())
                .addValue("locType", locId.getTypeAsString());
        String sql = SqlLocationQuery.GET_BY_CODE_AND_TYPE.getSql(schemaMap());
        LocationRowMapper locationRowMapper = new LocationRowMapper("LOC_", "RCTRHD_", locationCountyDao);
        return remoteNamedJdbc.queryForObject(sql, params, locationRowMapper);
    }

    /**
     * Retrieves all Locations including inactivated ones.
     */
    List<Location> getLocations() {
        return getLocations(true);
    }

    /**
     * Retrieves all Locations. Results can be limited to only active
     * locations by setting {@code activeOnly} to {@code true}
     *
     * @param activeOnly if true will only return active Location's.
     */
    List<Location> getLocations(boolean activeOnly) {
        String sql;
        if (activeOnly) {
            sql = SqlLocationQuery.GET_LOCATIONS.getSql(schemaMap());
        }
        else {
            sql = SqlLocationQuery.GET_LOCATIONS_INCLUDING_INACTIVE.getSql(schemaMap());
        }
        LocationRowMapper locationRowMapper = new LocationRowMapper("LOC_", "RCTRHD_", locationCountyDao);
        return remoteNamedJdbc.query(sql, locationRowMapper);
    }

    /**
     * Search locations by code.
     *
     * @return all locations with codes that contain the given <code>term</code>.
     */
    List<Location> searchLocations(String term) {
        MapSqlParameterSource params = new MapSqlParameterSource("term", "%" + StringUtils.upperCase(term) + "%");
        String sql = SqlLocationQuery.SEARCH_LOCATIONS.getSql(schemaMap());
        LocationRowMapper locationRowMapper = new LocationRowMapper("LOC_", "RCTRHD_", locationCountyDao);
        return remoteNamedJdbc.query(sql, params, locationRowMapper);
    }
}
