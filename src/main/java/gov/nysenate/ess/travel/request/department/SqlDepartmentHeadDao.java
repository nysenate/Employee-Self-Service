package gov.nysenate.ess.travel.request.department;

import gov.nysenate.ess.core.dao.base.BasicSqlQuery;
import gov.nysenate.ess.core.dao.base.DbVendor;
import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class SqlDepartmentHeadDao extends SqlBaseDao {

    /**
     * Retrieves the previously used department head emp id for the given employee.
     * If the user does not have any previously submitted apps, return 0.
     *
     * @param empId The employee id of the user submitting a travel app.
     */
    public int defaultDepartmentHead(int empId) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("submittedById", empId);
        String sql = SqlDepartmentHeadQuery.SELECT_DEFAULT_DEPT_HD.getSql(schemaMap());
        List<Integer> deptHeadList = localNamedJdbc.query(sql, params, new DepartmentHeadEmpIdRowMapper());
        return deptHeadList.stream()
                .findFirst()
                .orElse(0);
    }

    public boolean isDepartmentHead(int empId) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("empId", empId);
        String sql = SqlDepartmentHeadQuery.IS_DEPT_HEAD.getSql(schemaMap());
        return Boolean.TRUE.equals(localNamedJdbc.queryForObject(sql, params, Boolean.class));
    }

    /**
     * Retrieves a list of all employee id's that have been listed as a department head in a travel app.
     */
    public List<Integer> allDepartmentHeads() {
        String sql = SqlDepartmentHeadQuery.SELECT_DEPT_HDS.getSql(schemaMap());
        return localNamedJdbc.query(sql, new DepartmentHeadEmpIdRowMapper());
    }


    private class DepartmentHeadEmpIdRowMapper implements RowMapper<Integer> {

        @Override
        public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
            return rs.getInt("traveler_dept_head_emp_id");
        }
    }


    private enum SqlDepartmentHeadQuery implements BasicSqlQuery {
        SELECT_DEFAULT_DEPT_HD("""
                SELECT traveler_dept_head_emp_id
                FROM ${travelSchema}.app
                WHERE submitted_by_id = :submittedById
                ORDER BY created_date_time DESC
                LIMIT 1
                """
        ),
        IS_DEPT_HEAD("""
                SELECT EXISTS(
                  SELECT *
                  FROM ${travelSchema}.app
                  WHERE traveler_dept_head_emp_id = :empId
                )
                """
        ),
        SELECT_DEPT_HDS("""
                SELECT DISTINCT(traveler_dept_head_emp_id)
                FROM ${travelSchema}.app
                """
        );

        private String sql;

        SqlDepartmentHeadQuery(String sql) {
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
