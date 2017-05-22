package gov.nysenate.ess.core.dao.personnel.mapper;

import gov.nysenate.ess.core.dao.base.BaseRowMapper;
import gov.nysenate.ess.core.dao.unit.AddressRowMapper;
import gov.nysenate.ess.core.dao.unit.LocationRowMapper;
import gov.nysenate.ess.core.model.payroll.PayType;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.model.personnel.Gender;
import gov.nysenate.ess.core.model.personnel.MaritalStatus;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.stream.Stream;

import static gov.nysenate.ess.core.dao.base.SqlBaseDao.getLocalDate;
import static gov.nysenate.ess.core.dao.base.SqlBaseDao.getLocalDateTime;

public class EmployeeRowMapper extends BaseRowMapper<Employee>
{
    private String pfx = "";

    private AddressRowMapper addressRowMapper;
    private RespCenterRowMapper respCenterRowMapper;
    private LocationRowMapper locationRowMapper;

    public EmployeeRowMapper(String pfx, String rctrPfx, String rctrhdPfx, String agcyPfx, String locPfx) {
        this.pfx = pfx;
        this.addressRowMapper = new AddressRowMapper(pfx);
        this.respCenterRowMapper = new RespCenterRowMapper(rctrPfx, rctrhdPfx, agcyPfx);
        this.locationRowMapper = new LocationRowMapper(locPfx, rctrhdPfx);
    }

    @Override
    public Employee mapRow(ResultSet rs, int rowNum) throws SQLException {
        Employee emp = new Employee();
        emp.setEmployeeId(rs.getInt(pfx + "NUXREFEM"));
        emp.setSupervisorId(rs.getInt(pfx + "NUXREFSV"));
        emp.setActive(rs.getString(pfx + "CDEMPSTATUS").equals("A"));
        emp.setFirstName(rs.getString(pfx + "FFNAFIRST"));
        emp.setInitial(rs.getString(pfx + "FFNAMIDINIT"));
        emp.setLastName(rs.getString(pfx + "FFNALAST"));
        emp.setTitle(rs.getString(pfx + "FFNATITLE"));
        emp.setSuffix(rs.getString(pfx + "FFNASUFFIX"));
        emp.setEmail(rs.getString(pfx + "NAEMAIL"));
        emp.setHomePhone(rs.getString(pfx + "ADPHONENUM"));
        emp.setWorkPhone(rs.getString(pfx + "ADPHONENUMW"));
        emp.setJobTitle(rs.getString(pfx + "FFDEEMPTITLL"));
        emp.setPayType(rs.getString(pfx + "CDPAYTYPE") != null ? PayType.valueOf(rs.getString(pfx + "CDPAYTYPE")) : null);
        emp.setGender(rs.getString(pfx + "CDSEX") != null ? Gender.valueOf(rs.getString(pfx + "CDSEX")) : null);
        emp.setDateOfBirth(getLocalDate(rs, pfx + "DTBIRTH"));
        emp.setMaritalStatus(rs.getString(pfx + "CDMARITAL") != null ? MaritalStatus.valueOfCode(rs.getString(pfx + "CDMARITAL")) : null);
        emp.setNid(rs.getString(pfx + "NUEMPLID"));
        emp.setHomeAddress(addressRowMapper.mapRow(rs, rowNum));
        emp.setRespCenter(respCenterRowMapper.mapRow(rs, rowNum));
        emp.setWorkLocation(locationRowMapper.mapRow(rs, rowNum));
        emp.setSenateContServiceDate(getLocalDate(rs, pfx + "DTCONTSERV"));
        LocalDateTime maxUpdateDateTime = Stream.of(
                getLocalDateTime(rs, "DTTXNUPDATE"),
                getLocalDateTime(rs, "TTL_DTTXNUPDATE"),
                getLocalDateTime(rs, "ADDR_DTTXNUPDATE"),
                getLocalDateTime(rs, "XREF_DTTXNUPDATE"),
                getLocalDateTime(rs, "RCTR_DTTXNUPDATE"),
                getLocalDateTime(rs, "RCTRHD_DTTXNUPDATE"),
                getLocalDateTime(rs, "AGCY_DTTXNUPDATE"),
                getLocalDateTime(rs, "LOC_DTTXNUPDATE")
        )
                .filter(Objects::nonNull)
                .max(LocalDateTime::compareTo).orElse(null);

        emp.setUpdateDateTime(maxUpdateDateTime);

        if (emp.getEmail() != null) {
            emp.setUid(emp.getEmail().split("@")[0]);
        }
        return emp;
    }
}