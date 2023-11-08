package gov.nysenate.ess.core.dao.personnel.mapper;

import gov.nysenate.ess.core.dao.base.BaseRowMapper;
import gov.nysenate.ess.core.model.payroll.PayType;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.model.personnel.Gender;
import gov.nysenate.ess.core.model.personnel.MaritalStatus;
import gov.nysenate.ess.core.model.personnel.PersonnelStatus;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import static gov.nysenate.ess.core.dao.base.SqlBaseDao.getLocalDate;

public class MinimalEmployeeRowMapper extends BaseRowMapper<Employee> {
    private final String pfx;

    public MinimalEmployeeRowMapper(String pfx) {
        this.pfx = pfx;
    }

    @Override
    public Employee mapRow(ResultSet rs, int rowNum) throws SQLException {
        Employee emp = new Employee();
        emp.setEmployeeId(rs.getInt(pfx + "NUXREFEM"));
        emp.setSupervisorId(rs.getInt(pfx + "NUXREFSV"));
        emp.setActive(rs.getString(pfx + "CDEMPSTATUS").equals("A"));
        emp.setPersonnelStatus(Optional.ofNullable(rs.getString(pfx + "CDSTATPER"))
                .map(PersonnelStatus::valueOf)
                .orElse(null));
        emp.setFirstName(rs.getString(pfx + "FFNAFIRST"));
        emp.setInitial(rs.getString(pfx + "FFNAMIDINIT"));
        emp.setLastName(rs.getString(pfx + "FFNALAST"));
        emp.setTitle(rs.getString(pfx + "FFNATITLE"));
        emp.setSuffix(rs.getString(pfx + "FFNASUFFIX"));
        emp.setEmail(rs.getString(pfx + "NAEMAIL"));
        emp.setHomePhone(rs.getString(pfx + "ADPHONENUM"));
        emp.setWorkPhone(rs.getString(pfx + "ADPHONENUMW"));
        emp.setPayType(rs.getString(pfx + "CDPAYTYPE") != null ? PayType.valueOf(rs.getString(pfx + "CDPAYTYPE")) : null);
        emp.setGender(rs.getString(pfx + "CDSEX") != null ? Gender.valueOf(rs.getString(pfx + "CDSEX")) : null);
        emp.setDateOfBirth(getLocalDate(rs, pfx + "DTBIRTH"));
        emp.setMaritalStatus(rs.getString(pfx + "CDMARITAL") != null ? MaritalStatus.valueOfCode(rs.getString(pfx + "CDMARITAL")) : null);
        emp.setSenateContServiceDate(getLocalDate(rs, pfx + "DTCONTSERV"));

        if (emp.getEmail() != null) {
            emp.setUid(emp.getEmail().split("@")[0]);
        }
        return emp;
    }
}
