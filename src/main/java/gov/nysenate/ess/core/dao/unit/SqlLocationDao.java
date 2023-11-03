package gov.nysenate.ess.core.dao.unit;

import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import gov.nysenate.ess.core.model.unit.Location;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SqlLocationDao extends SqlBaseDao {
    @Autowired private SqlLocationCountyDao locationCountyDao;

    /**
     * Retrieves all Locations. Results can be limited to only active
     * locations by setting {@code activeOnly} to {@code true}
     */
    public List<Location> getAllLocations() {
        String sql = SqlLocationQuery.GET_LOCATIONS_INCLUDING_INACTIVE.getSql(schemaMap());
        LocationRowMapper locationRowMapper = new LocationRowMapper("LOC_", "RCTRHD_", locationCountyDao);
        return remoteNamedJdbc.query(sql, locationRowMapper);
    }

    /**
     * Search locations by code.
     *
     * @return all locations with codes that contain the given <code>term</code>.
     */
    public List<Location> searchLocations(String term) {
        MapSqlParameterSource params = new MapSqlParameterSource("term", "%" + StringUtils.upperCase(term) + "%");
        String sql = SqlLocationQuery.SEARCH_LOCATIONS.getSql(schemaMap());
        LocationRowMapper locationRowMapper = new LocationRowMapper("LOC_", "RCTRHD_", locationCountyDao);
        return remoteNamedJdbc.query(sql, params, locationRowMapper);
    }
}
