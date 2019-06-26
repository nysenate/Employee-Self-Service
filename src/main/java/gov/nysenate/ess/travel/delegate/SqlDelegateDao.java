package gov.nysenate.ess.travel.delegate;

import com.google.common.base.Preconditions;
import gov.nysenate.ess.core.dao.base.BaseRowMapper;
import gov.nysenate.ess.core.dao.base.BasicSqlQuery;
import gov.nysenate.ess.core.dao.base.DbVendor;
import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public class SqlDelegateDao extends SqlBaseDao implements DelegateDao {

    private EmployeeInfoService employeeInfoService;

    @Autowired
    public SqlDelegateDao(EmployeeInfoService employeeInfoService) {
        this.employeeInfoService = employeeInfoService;
    }

    /**
     * Select delegates assigned by the given principalId which are active on date.
     * In this case, active includes delegates which have been entered but have not started yet (i.e. startDate > date)
     * Delegates are inactive if their end date has passed (i.e. endDate < date)
     *
     * @param principalId The employeeId of the principal employee.
     * @param date        Returns delegates which have not expired as of this date.
     * @return
     */
    @Override
    public List<Delegate> activeDelegates(int principalId, LocalDate date) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("principalId", principalId)
                .addValue("date", toDate(date));
        String sql = SqlDelegateQuery.SELECT_DELEGATES.getSql(schemaMap());
        RowMapper mapper = new DelegateRowMapper(employeeInfoService);
        return localNamedJdbc.query(sql, params, mapper);
    }

    /**
     * Saves a collection of delegates for a single Principal employee.
     * All delegates should have the same principal Employee.
     */
    @Override
    @Transactional(value = "localTxManager")
    public void saveDelegates(List<Delegate> delegates, int principalId) {
        if (delegates == null) {
            return;
        }
        Preconditions.checkArgument(delegates.stream().allMatch(d -> d.principal.getEmployeeId() == principalId));

        deletePrincipalDelegates(principalId);
        for (Delegate d : delegates) {
            insertDelegate(d);
        }
    }

    /**
     * Finds a delegate by delegate emp id.
     * A user should only ever be assigned as a delegate for a single reviewer at a time so this
     * only returns one Delegate object.
     * @param delegateEmpId
     * @param date
     * @return
     */
    @Override
    public Optional<Delegate> delegateAssignedToEmp(int delegateEmpId, LocalDate date) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("delegateEmpId", delegateEmpId)
                .addValue("date", toDate(date));
        String sql = SqlDelegateQuery.SELECT_DELEGATE_ASSIGNMENT.getSql(schemaMap());
        try {
            return Optional.of(localNamedJdbc.queryForObject(sql, params, new DelegateRowMapper(employeeInfoService)));
        } catch (IncorrectResultSizeDataAccessException ex) {
            return Optional.empty();
        }
    }

    private void deletePrincipalDelegates(int principalId) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("principalId", principalId);
        String sql = SqlDelegateQuery.DELETE_DELEGATES_FOR_PRINCIPAL.getSql(schemaMap());
        localNamedJdbc.update(sql, params);
    }

    private void insertDelegate(Delegate delegate) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("principalEmpId", delegate.principal.getEmployeeId())
                .addValue("delegateEmpId", delegate.delegate.getEmployeeId())
                .addValue("startDate", toDate(delegate.startDate))
                .addValue("endDate", toDate(delegate.endDate));
        String sql = SqlDelegateQuery.INSERT_DELEGATE.getSql(schemaMap());
        KeyHolder keyHolder = new GeneratedKeyHolder();
        localNamedJdbc.update(sql, params, keyHolder);
        delegate.id = (Integer) keyHolder.getKeys().get("delegate_id");
    }

    private enum SqlDelegateQuery implements BasicSqlQuery {
        SELECT_DELEGATES(
                "SELECT delegate_id, principal_emp_id, delegate_emp_id, start_date, end_date\n" +
                        " FROM ${travelSchema}.delegate\n" +
                        " WHERE principal_emp_id = :principalId\n" +
                        " AND end_date > :date OR end_date = :date"
        ),
        INSERT_DELEGATE(
                "INSERT INTO ${travelSchema}.delegate(principal_emp_id, delegate_emp_id, start_date, end_date)\n" +
                        " VALUES(:principalEmpId, :delegateEmpId, :startDate, :endDate)"
        ),
        DELETE_DELEGATES_FOR_PRINCIPAL(
                "DELETE FROM ${travelSchema}.delegate WHERE principal_emp_id = :principalId"
        ),
        SELECT_DELEGATE_ASSIGNMENT(
                "SELECT delegate_id, principal_emp_id, delegate_emp_id, start_date, end_date\n" +
                        " FROM ${travelSchema}.delegate\n" +
                        " WHERE delegate_emp_id = :delegateEmpId\n" +
                        " AND (start_date < :date OR start_date = :date)\n" +
                        " AND (end_date > :date OR end_date = :date)"
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
