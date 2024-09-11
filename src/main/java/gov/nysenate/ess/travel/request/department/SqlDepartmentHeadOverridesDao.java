package gov.nysenate.ess.travel.request.department;

import gov.nysenate.ess.core.dao.base.BaseHandler;
import gov.nysenate.ess.core.dao.base.BasicSqlQuery;
import gov.nysenate.ess.core.dao.base.DbVendor;
import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

@Repository
public class SqlDepartmentHeadOverridesDao extends SqlBaseDao {

    public Map<Integer, Integer> currentOverrides() {
        MapSqlParameterSource params = new MapSqlParameterSource("date", toDate(LocalDate.now()));
        String sql = SqlDepartmentHeadOverridesQuery.SELECT_OVERRIDES.getSql(schemaMap());
        DepartmentHeadOverridesHandler handler = new DepartmentHeadOverridesHandler();
        localNamedJdbc.query(sql, params, handler);
        return handler.getResults();
    }

    /**
     * Returns an optional containing the employees department head employee id if they are included
     * in the overrides table. Otherwise, returns an empty Optional.
     * @param employeeId Try to find a department head override for this employee id.
     */
    public Optional<Integer> departmentHeadOverrideForEmployee(int employeeId) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("employeeId", employeeId)
                .addValue("date", toDate(LocalDate.now()));
        String sql = SqlDepartmentHeadOverridesQuery.SELECT_DEPT_HD_FOR_EMP.getSql(schemaMap());
        try {
            return Optional.ofNullable(localNamedJdbc.queryForObject(sql, params, Integer.class));
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }

    /**
     * @return A Set of EmployeeId's for all department heads currently active in the overrides table.
     */
    public Set<Integer> currentDepartmentHeads() {
        MapSqlParameterSource params = new MapSqlParameterSource("date", toDate(LocalDate.now()));
        String sql = SqlDepartmentHeadOverridesQuery.SELECT_DEPT_HDS.getSql(schemaMap());
        return new HashSet<>(localNamedJdbc.queryForList(sql, params, Integer.class));
    }


    private enum SqlDepartmentHeadOverridesQuery implements BasicSqlQuery {
        SELECT_OVERRIDES("""
                SELECT employee_id, department_head_emp_id
                FROM ${essSchema}.department_head_override
                WHERE effective_date_range @> :date::date
                """),
        SELECT_DEPT_HD_FOR_EMP("""
                SELECT department_head_emp_id
                FROM ${essSchema}.department_head_override
                WHERE employee_id = :employeeId
                  AND effective_date_range @> :date::date
                """),
        SELECT_DEPT_HDS("""
                SELECT distinct(department_head_emp_id)
                FROM ${essSchema}.department_head_override
                WHERE effective_date_range @> :date::date
                """)
        ;

        private String sql;

        SqlDepartmentHeadOverridesQuery(String sql) {
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

    private class DepartmentHeadOverridesHandler extends BaseHandler {

        private Map<Integer, Integer> empIdToDeptHdId;

        public DepartmentHeadOverridesHandler() {
            this.empIdToDeptHdId = new HashMap<>();
        }

        @Override
        public void processRow(ResultSet rs) throws SQLException {
            Integer empId = rs.getInt("employee_id");
            Integer deptHdId = rs.getInt("department_head_emp_id");
            empIdToDeptHdId.put(empId, deptHdId);
        }

        protected Map<Integer, Integer> getResults() {
            return empIdToDeptHdId;
        }
    }
}
