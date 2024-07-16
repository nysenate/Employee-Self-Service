package gov.nysenate.ess.travel.request.department;

import gov.nysenate.ess.core.dao.base.BasicSqlQuery;
import gov.nysenate.ess.core.dao.base.DbVendor;
import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository
public class SqlDepartmentHeadDao extends SqlBaseDao {

    /**
     * Retrieves all the current department heads employee id's.
     */
    public Set<Integer> currentDeptHdIds() {
        return deptHdIdsAsOf(LocalDate.now());
    }

    private Set<Integer> deptHdIdsAsOf(LocalDate date) {
        MapSqlParameterSource params = new MapSqlParameterSource("date", toDate(date));
        String sql = SqlDepartmentHeadQuery.SELECT_ALL_DEPT_HD_EMP_IDS.getSql(schemaMap());
        return new HashSet<>(localNamedJdbc.queryForList(sql, params, Integer.class));
    }

    private enum SqlDepartmentHeadQuery implements BasicSqlQuery {
        SELECT_ALL_DEPT_HD_EMP_IDS("""
                SELECT employee_id
                FROM ${essSchema}.department_head
                WHERE effective_date_range @> :date::date
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
