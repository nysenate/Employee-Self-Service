package gov.nysenate.ess.core.dao.unit;

import gov.nysenate.ess.core.dao.base.BaseRowMapper;
import gov.nysenate.ess.core.dao.personnel.mapper.RespHeadRowMapper;
import gov.nysenate.ess.core.model.personnel.ResponsibilityHead;
import gov.nysenate.ess.core.model.unit.Address;
import gov.nysenate.ess.core.model.unit.Location;
import gov.nysenate.ess.core.model.unit.LocationId;

import java.sql.ResultSet;
import java.sql.SQLException;

public class LocationRowMapper extends BaseRowMapper<Location>
{
    private String pfx;

    private LocationIdRowMapper locationIdRowMapper;
    private RespHeadRowMapper respHeadRowMapper;
    private SqlLocationCountyDao locationCountyDao;

    public LocationRowMapper(String locPfx, String rctrhdPfx, SqlLocationCountyDao locationCountyDao) {
        this.pfx = locPfx;
        this.locationIdRowMapper = new LocationIdRowMapper(locPfx);
        this.respHeadRowMapper = new RespHeadRowMapper(rctrhdPfx);
        this.locationCountyDao = locationCountyDao;
    }

    @Override
    public Location mapRow(ResultSet rs, int rowNum) throws SQLException {
        LocationId locId = locationIdRowMapper.mapRow(rs, rowNum);
        Address addr = new Address();
        addr.setAddr1(rs.getString(pfx + "FFADSTREET1"));
        addr.setAddr2(rs.getString(pfx + "FFADSTREET2"));
        addr.setCity(rs.getString(pfx + "FFADCITY"));
        addr.setState(rs.getString(pfx + "ADSTATE"));
        addr.setZip5(rs.getString(pfx + "ADZIPCODE"));
        addr.setCounty(locationCountyDao.getCounty(locId));
        ResponsibilityHead rspHead = respHeadRowMapper.mapRow(rs, rowNum);
        String locationDescription = rs.getString(pfx + "DELOCAT");
        String isActiveString = rs.getString(pfx + "CDSTATUS");
        boolean isActive = isActiveString.equals("A");
        return new Location(locId, addr, rspHead, locationDescription, isActive);
    }
}
