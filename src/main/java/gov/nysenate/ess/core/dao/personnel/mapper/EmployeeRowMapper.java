package gov.nysenate.ess.core.dao.personnel.mapper;

import gov.nysenate.ess.core.dao.base.BaseRowMapper;
import gov.nysenate.ess.core.dao.unit.AddressRowMapper;
import gov.nysenate.ess.core.dao.unit.LocationIdRowMapper;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.service.base.LocationService;

import javax.annotation.Nonnull;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.stream.Stream;

import static gov.nysenate.ess.core.dao.base.SqlBaseDao.getLocalDateTime;

public class EmployeeRowMapper extends BaseRowMapper<Employee> {
    private final String pfx;
    private final AddressRowMapper addressRowMapper;
    private final RespCenterRowMapper respCenterRowMapper;
    private final LocationIdRowMapper locationIdRowMapper;
    private final LocationService locationService;

    public EmployeeRowMapper(String pfx, String rctrPfx, String rctrhdPfx, String agcyPfx, String locPfx, LocationService locationService) {
        this.pfx = pfx;
        this.addressRowMapper = new AddressRowMapper(pfx);
        this.respCenterRowMapper = new RespCenterRowMapper(rctrPfx, rctrhdPfx, agcyPfx);
        this.locationIdRowMapper = new LocationIdRowMapper(locPfx);
        this.locationService = locationService;
    }

    @Override
    public Employee mapRow(@Nonnull ResultSet rs, int rowNum) throws SQLException {
        Employee emp = new MinimalEmployeeRowMapper(pfx).mapRow(rs, rowNum);
        emp.setNid(rs.getString(pfx + "NUEMPLID"));
        emp.setJobTitle(rs.getString(pfx + "FFDEEMPTITLL"));
        emp.setHomeAddress(addressRowMapper.mapRow(rs, rowNum));
        emp.setRespCenter(respCenterRowMapper.mapRow(rs, rowNum));
        emp.setWorkLocation(locationService.getLocation(locationIdRowMapper.mapRow(rs, rowNum)));
        emp.setUpdateDateTime(Stream.of(
                getLocalDateTime(rs, "DTTXNUPDATE"),
                getLocalDateTime(rs, "TTL_DTTXNUPDATE"),
                getLocalDateTime(rs, "ADDR_DTTXNUPDATE"),
                getLocalDateTime(rs, "XREF_DTTXNUPDATE"),
                getLocalDateTime(rs, "RCTR_DTTXNUPDATE"),
                getLocalDateTime(rs, "RCTRHD_DTTXNUPDATE"),
                getLocalDateTime(rs, "AGCY_DTTXNUPDATE"),
                getLocalDateTime(rs, "LOC_DTTXNUPDATE")
        ).filter(Objects::nonNull).max(LocalDateTime::compareTo).orElse(null));
        return emp;
    }
}
