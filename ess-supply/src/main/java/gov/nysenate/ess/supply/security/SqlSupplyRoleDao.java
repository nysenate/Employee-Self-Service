package gov.nysenate.ess.supply.security;

import gov.nysenate.ess.core.dao.base.BaseRowMapper;
import gov.nysenate.ess.core.dao.base.BasicSqlQuery;
import gov.nysenate.ess.core.dao.base.DbVendor;
import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import gov.nysenate.ess.core.model.auth.SenatePerson;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.model.unit.Location;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import gov.nysenate.ess.core.service.unit.LocationService;
import gov.nysenate.ess.supply.security.role.SupplyEmployee;
import gov.nysenate.ess.supply.security.role.SupplyManager;
import gov.nysenate.ess.supply.security.role.SupplyRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.apache.shiro.web.filter.mgt.DefaultFilter.roles;

@Service
public class SqlSupplyRoleDao extends SqlBaseDao implements SupplyRoleDao {

    @Autowired private EmployeeInfoService employeeService;
    @Autowired private LocationService locationService;

    @Override
    public List<SupplyRole> getSupplyRoles(SenatePerson person) {
        MapSqlParameterSource params = new MapSqlParameterSource("uid", person.getUid());
        String sql = SqlSupplyRoleQuery.GET_ROLE_BY_UID.getSql(schemaMap());
        List<Integer> secLevels = remoteNamedJdbc.query(sql, params, (rs, i) -> {
            return rs.getInt("cdseclevel");
        });
        return mapSecLevelsToRoles(person, secLevels);
    }

    private List<SupplyRole> mapSecLevelsToRoles(SenatePerson person, List<Integer> secLevels) {
        String locationId = getEmployeesLocationId(person);
        List<SupplyRole> roles = new ArrayList<>();
        for (Integer secLevel: secLevels) {
            switch(secLevel) {
                case 0:
                    roles.add(new SupplyEmployee(person.getEmployeeId(), locationId));
                    break;
                case 1:
                    roles.add(new SupplyManager(person.getEmployeeId(), locationId));
                    break;
            }
        }
        return roles;
    }

    private String getEmployeesLocationId(SenatePerson person) {
        Employee employee = employeeService.getEmployee(person.getEmployeeId());
        Location location = locationService.getLocation(employee.getWorkLocation().getCode(), employee.getWorkLocation().getType());
        return location.getCode() + "-" + location.getType().getCode();
    }

    private enum SqlSupplyRoleQuery implements BasicSqlQuery {

        GET_ROLE_BY_UID("SELECT cdseclevel \n" +
                        "FROM ${masterSchema}.im86modmenu \n" +
                        "WHERE defrmint = 'FSSUPREQ12' \n" +
                        "AND numodule = 'F01200' \n" +
                        "AND cdstatus = 'A' \n" +
                        "AND nauser = :uid");

        private String sql;

        SqlSupplyRoleQuery(String sql) {
            this.sql = sql;
        }

        @Override
        public String getSql() {
            return this.sql;
        }

        @Override
        public DbVendor getVendor() {
            return DbVendor.ORACLE_10g;
        }
    }
}
