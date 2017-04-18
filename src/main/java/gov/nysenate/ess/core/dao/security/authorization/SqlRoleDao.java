package gov.nysenate.ess.core.dao.security.authorization;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import gov.nysenate.ess.core.dao.base.BasicSqlQuery;
import gov.nysenate.ess.core.dao.base.DbVendor;
import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import gov.nysenate.ess.core.model.auth.EssRole;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.EnumSet;
import java.util.List;

@Repository
public class SqlRoleDao extends SqlBaseDao implements RoleDao {

    @Autowired private EmployeeInfoService employeeInfoService;

    /** {@inheritDoc} */
    public ImmutableSet<EssRole> getRoles(Employee employee) {
        MapSqlParameterSource params = new MapSqlParameterSource("employeeId", employee.getEmployeeId());
        String sql = SqlRoleQuery.GET_EMPLOYEE_ROLES.getSql(schemaMap());
        List<EssRole> roles = localNamedJdbc.query(sql, params,
                (rs, i) -> EssRole.valueOf(rs.getString("role")));
        // Everyone has the senate employee role by default.
        roles.add(EssRole.SENATE_EMPLOYEE);
        // Add the senator role if the employee is a senator
        if (employee.isSenator()) {
            roles.add(EssRole.SENATOR);
        }
        return ImmutableSet.copyOf(EnumSet.copyOf(roles));
    }

    @Override
    public ImmutableList<Employee> getEmployeesWithRole(EssRole role) {
        MapSqlParameterSource params = new MapSqlParameterSource("role", role.toString());
        String sql = SqlRoleQuery.GET_EMPLOYEES_BY_ROLE.getSql(schemaMap());
        List<Employee> employees = localNamedJdbc.query(sql, params, new EmployeeRoleMapper(employeeInfoService));
        return ImmutableList.copyOf(employees);
    }

    protected enum SqlRoleQuery implements BasicSqlQuery {
        GET_EMPLOYEE_ROLES(
                "SELECT * FROM ${essSchema}.user_roles \n" +
                "WHERE employee_id = :employeeId"
        ),
        GET_EMPLOYEES_BY_ROLE(
                "SELECT employee_id FROM ${essSchema}.user_roles \n" +
                "WHERE role = :role::${essSchema}.ess_role"
        )
        ;

        private String sql;

        SqlRoleQuery(String sql) {
            this.sql = sql;
        }

        @Override
        public String getSql() {
            return this.sql;
        }

        @Override
        public DbVendor getVendor() {
            return DbVendor.POSTGRES;
        }
    }

    private class EmployeeRoleMapper implements RowMapper<Employee> {

        private EmployeeInfoService employeeInfoService;

        protected EmployeeRoleMapper(EmployeeInfoService employeeInfoService) {
            this.employeeInfoService = employeeInfoService;
        }

        @Override
        public Employee mapRow(ResultSet rs, int i) throws SQLException {
            return employeeInfoService.getEmployee(rs.getInt("employee_id"));
        }
    }
}
