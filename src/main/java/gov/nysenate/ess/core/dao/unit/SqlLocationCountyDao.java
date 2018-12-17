package gov.nysenate.ess.core.dao.unit;

import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import gov.nysenate.ess.core.model.unit.LocationId;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

/**
 * This repository retrieves the county of Senate work addresses which is very important to travel applications.
 *
 * Senate work addresses are stored in SFMS but the county of the address is not included.
 * Since work addresses are not valid addresses its often not possible to get the county via geocoding or other means.
 * Therefore, we created the 'work_location_county' table in postgres to store manually entered county data.
 */
@Repository
public class SqlLocationCountyDao extends SqlBaseDao {

    String getCounty(LocationId locationId) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("locCode", locationId.getCode())
                .addValue("locType", locationId.getTypeAsString());
        String sql = SqlLocationCountyQuery.SELECT_WORK_ADDRESS_COUNTY.getSql(schemaMap());

        String county;
        try {
            county = localNamedJdbc.queryForObject(sql, params, (rs, rowNum) -> rs.getString("county"));
        }
        catch(IncorrectResultSizeDataAccessException ex) {
            // If no county has been entered for this location return an empty string.
            county = "";
        }
        return county;
    }
}
