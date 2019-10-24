package gov.nysenate.ess.core.dao.unit;

import gov.nysenate.ess.core.dao.base.BaseRowMapper;
import gov.nysenate.ess.core.model.unit.LocationId;
import gov.nysenate.ess.core.model.unit.LocationType;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class LocationIdRowMapper extends BaseRowMapper<LocationId> {

    private String pfx;

    public LocationIdRowMapper(String pfx) {
        this.pfx = pfx;
    }

    @Override
    public LocationId mapRow(ResultSet rs, int rowNum) throws SQLException {
        LocationType locType = Optional.ofNullable(rs.getString(pfx + "CDLOCTYPE"))
                .map(locStr -> locStr.charAt(0))
                .map(LocationType::valueOfCode)
                .orElse(null);
        return new LocationId(rs.getString(pfx + "CDLOCAT"), locType);
    }
}
