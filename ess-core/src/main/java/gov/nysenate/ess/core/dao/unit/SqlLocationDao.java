package gov.nysenate.ess.core.dao.unit;

import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import gov.nysenate.ess.core.model.unit.Location;
import gov.nysenate.ess.core.model.unit.LocationId;
import gov.nysenate.ess.core.model.unit.LocationType;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SqlLocationDao extends SqlBaseDao implements LocationDao {

    @Override
    public List<Location> getLocations() {
        String sql = SqlLocationQuery.GET_LOCATIONS.getSql(schemaMap());
        LocationRowMapper locationRowMapper = new LocationRowMapper("LOC_", "RCTRHD_");
        return remoteNamedJdbc.query(sql, locationRowMapper);
    }

    @Override
    public Location getLocationById(LocationId locId) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("locCode", locId.getCode())
                .addValue("locType", locId.getTypeAsString());
        String sql = SqlLocationQuery.GET_BY_CODE_AND_TYPE.getSql(schemaMap());
        LocationRowMapper locationRowMapper = new LocationRowMapper("LOC_", "RCTRHD_");
        return remoteNamedJdbc.queryForObject(sql, params, locationRowMapper);
    }
}
