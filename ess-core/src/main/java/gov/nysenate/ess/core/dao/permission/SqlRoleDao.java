package gov.nysenate.ess.core.dao.permission;

import com.google.common.collect.ImmutableList;
import gov.nysenate.ess.core.dao.base.BasicSqlQuery;
import gov.nysenate.ess.core.dao.base.DbVendor;
import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import gov.nysenate.ess.core.model.permission.EssRole;
import gov.nysenate.ess.core.model.personnel.Employee;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SqlRoleDao extends SqlBaseDao implements RoleDao {

    /** {@inheritDoc} */
    public ImmutableList<EssRole> getRoles(Employee employee) {
        MapSqlParameterSource params = new MapSqlParameterSource("employeeId", employee.getEmployeeId());
        String sql = SqlRoleQuery.GET_EMPLOYEE_ROLES.getSql(schemaMap());
        List<EssRole> roles = localNamedJdbc.query(sql, params, ((rs, i) -> {
            return EssRole.valueOf(rs.getString("role"));
        }));
        // Everyone has the senate employee role by default.
        roles.add(EssRole.SENATE_EMPLOYEE);
        return ImmutableList.copyOf(roles);
    }

    public enum SqlRoleQuery implements BasicSqlQuery {
        GET_EMPLOYEE_ROLES(
                "SELECT * FROM ${essSchema}.user_roles \n" +
                "WHERE employee_id = :employeeId"
        );

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
}
