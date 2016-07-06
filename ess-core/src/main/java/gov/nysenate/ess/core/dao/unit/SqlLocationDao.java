package gov.nysenate.ess.core.dao.unit;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import gov.nysenate.ess.core.model.personnel.ResponsibilityHead;
import gov.nysenate.ess.core.model.unit.Location;
import gov.nysenate.ess.core.model.unit.LocationId;
import gov.nysenate.ess.core.model.unit.LocationType;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SqlLocationDao extends SqlBaseDao implements LocationDao {

    private final Logger logger = LoggerFactory.getLogger(SqlLocationDao.class);

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

    @Override
    public ImmutableCollection<Location> searchLocations(String term) {
        MapSqlParameterSource params = new MapSqlParameterSource("term", "%" + StringUtils.upperCase(term) + "%");
        String sql = SqlLocationQuery.SEARCH_LOCATIONS.getSql(schemaMap());
        LocationRowMapper locationRowMapper = new LocationRowMapper("LOC_", "RCTRHD_");
        return ImmutableList.copyOf(remoteNamedJdbc.query(sql, params, locationRowMapper));
    }

    /** {@inheritDoc} */
    @Override
    public List<Location> getLocationsByResponsibilityHead(ResponsibilityHead responsibilityHead) {
        MapSqlParameterSource params = new MapSqlParameterSource("responsibilityHead", responsibilityHead.getCode());
        String sql = SqlLocationQuery.GET_LOCATIONS_BY_RESPONSIBILITY_HEAD.getSql(schemaMap());
        LocationRowMapper locationRowMapper = new LocationRowMapper("LOC_", "RCTRHD_");
        return remoteNamedJdbc.query(sql, params, locationRowMapper);
    }
}
