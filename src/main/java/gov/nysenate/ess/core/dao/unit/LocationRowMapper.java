package gov.nysenate.ess.core.dao.unit;

import gov.nysenate.ess.core.dao.base.BaseRowMapper;
import gov.nysenate.ess.core.dao.personnel.mapper.RespHeadRowMapper;
import gov.nysenate.ess.core.model.personnel.ResponsibilityHead;
import gov.nysenate.ess.core.model.unit.Address;
import gov.nysenate.ess.core.model.unit.Location;
import gov.nysenate.ess.core.model.unit.LocationId;
import gov.nysenate.ess.core.model.unit.LocationType;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class LocationRowMapper extends BaseRowMapper<Location>
{
    private String pfx;

    private RespHeadRowMapper respHeadRowMapper;

    public LocationRowMapper(String locPfx, String rctrhdPfx) {
        this.pfx = locPfx;
        this.respHeadRowMapper = new RespHeadRowMapper(rctrhdPfx);
    }

    @Override
    public Location mapRow(ResultSet rs, int rowNum) throws SQLException {
        LocationType locType = Optional.ofNullable(rs.getString(pfx + "CDLOCTYPE"))
                .map(locStr -> locStr.charAt(0))
                .map(LocationType::valueOfCode)
                .orElse(null);
        LocationId locId = new LocationId(rs.getString(pfx + "CDLOCAT"), locType);
        Address addr = new Address();
        addr.setAddr1(rs.getString(pfx + "FFADSTREET1"));
        addr.setAddr2(rs.getString(pfx + "FFADSTREET2"));
        addr.setCity(rs.getString(pfx + "FFADCITY"));
        addr.setState(rs.getString(pfx + "ADSTATE"));
        addr.setZip5(rs.getString(pfx + "ADZIPCODE"));
        ResponsibilityHead rspHead = respHeadRowMapper.mapRow(rs, rowNum);
        String locationDescription = rs.getString(pfx + "DELOCAT");
        return new Location(locId, addr, rspHead, locationDescription);
    }
}
