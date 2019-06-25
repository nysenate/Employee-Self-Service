package gov.nysenate.ess.travel.delegate;

import gov.nysenate.ess.core.dao.base.BaseRowMapper;
import gov.nysenate.ess.core.dao.base.BasicSqlQuery;
import gov.nysenate.ess.core.dao.base.DbVendor;
import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

@Repository
public class SqlDelegateDao extends SqlBaseDao {

    private EmployeeInfoService employeeInfoService;

    @Autowired
    public SqlDelegateDao(EmployeeInfoService employeeInfoService) {
        this.employeeInfoService = employeeInfoService;
    }

    public List<Delegate> selectDelegates(int principalId, LocalDate date) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("principalId", principalId)
                .addValue("date", toDate(date.plusDays(1))); // Add one day so we can do > instead of > or = in sql query.
        String sql = SqlDelegateQuery.SELECT_DELEGATES.getSql(schemaMap());
        RowMapper mapper = new DelegateRowMapper(employeeInfoService);
        return localNamedJdbc.query(sql, params, mapper);
    }

    public void saveDelegate(List<Delegate> delegates) {
        for (Delegate d : delegates) {
            if (!insertDelegate(d)) {
                updateDelegate(d);
            }
        }
    }

    private boolean insertDelegate(Delegate delegate) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("principalEmpId", delegate.principal.getEmployeeId())
                .addValue("delegateEmpId", delegate.delegate.getEmployeeId())
                .addValue("startDate", toDate(delegate.startDate))
                .addValue("endDate", toDate(delegate.endDate));
        String sql = SqlDelegateQuery.INSERT_DELEGATE.getSql(schemaMap());
        KeyHolder keyHolder = new GeneratedKeyHolder();
        boolean success = localNamedJdbc.update(sql, params, keyHolder) == 1;
        if (success) {
            delegate.id = (Integer) keyHolder.getKeys().get("delegate_id");
            return true;
        } else {
            return false;
        }
    }

    private void updateDelegate(Delegate delegate) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("delegateId", delegate.id);
        String sql = SqlDelegateQuery.UPDATE_DELEGATE.getSql(schemaMap());
        localNamedJdbc.update(sql, params);
    }


    private enum SqlDelegateQuery implements BasicSqlQuery {
        SELECT_DELEGATES(
                "SELECT delegate_id, principal_emp_id, delegate_emp_id, start_date, end_date\n" +
                        " FROM ${travelSchema}.delegate\n" +
                        " WHERE principal_emp_id = :principalId\n" +
                        " AND end_date > :date"
        ),
        INSERT_DELEGATE(
                "INSERT INTO ${travelSchema}.delegate(principal_emp_id, delegate_emp_id, start_date, end_date)\n" +
                        " VALUES(:principalEmpId, :delegateEmpId, :startDate, :endDate)"
        ),
        UPDATE_DELEGATE(
                "UPDATE ${travelSchema}.delegate SET start_date = :startDate, end_date = :endDate\n" +
                        " WHERE delegate_id = :delegateId"
        );

        private String sql;

        SqlDelegateQuery(String sql) {
            this.sql = sql;
        }

        @Override
        public String getSql() {
            return sql;
        }

        @Override
        public DbVendor getVendor() {
            return DbVendor.POSTGRES;
        }
    }

    private class DelegateRowMapper extends BaseRowMapper<Delegate> {

        private EmployeeInfoService employeeInfoService;

        public DelegateRowMapper(EmployeeInfoService employeeInfoService) {
            this.employeeInfoService = employeeInfoService;
        }

        @Override
        public Delegate mapRow(ResultSet rs, int i) throws SQLException {
            return new Delegate(
                    rs.getInt("delegate_id"),
                    employeeInfoService.getEmployee(rs.getInt("principal_emp_id")),
                    employeeInfoService.getEmployee(rs.getInt("delegate_emp_id")),
                    getLocalDateFromRs(rs, "start_date"),
                    getLocalDateFromRs(rs, "end_date")
            );
        }
    }
}
