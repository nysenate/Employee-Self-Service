package gov.nysenate.ess.travel.delegate;

import com.google.common.base.Preconditions;
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
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class SqlDelegationDao extends SqlBaseDao implements DelegationDao {

    private EmployeeInfoService employeeInfoService;

    @Autowired
    public SqlDelegationDao(EmployeeInfoService employeeInfoService) {
        this.employeeInfoService = employeeInfoService;
    }

    /**
     * Finds all delegations assigned by the given principalId
     *
     * @param principalId The employeeId of the principal employee.
     * @return
     */
    @Override
    public List<Delegation> findByPrincipalEmpId(int principalId) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("principalId", principalId);
        String sql = SqlDelegateQuery.SELECT_DELEGATES.getSql(schemaMap());
        RowMapper<Delegation> mapper = new DelegateRowMapper(employeeInfoService);
        return localNamedJdbc.query(sql, params, mapper);
    }

    /**
     * Saves a collection of delegations for a single Principal employee.
     * All delegates should have the same principal Employee.
     */
    @Override
    @Transactional(value = "localTxManager")
    public void save(List<Delegation> delegations, int principalId) {
        if (delegations == null) {
            return;
        }
        Preconditions.checkArgument(delegations.stream().allMatch(d -> d.principal.getEmployeeId() == principalId));

        deletePrincipalDelegations(principalId);
        for (Delegation d : delegations) {
            insertDelegation(d);
        }
    }

    /**
     * Finds delegations that have been granted to an employee.
     * @param delegateEmpId
     * @return A list of delegates which have been granted to this employee. This returns all delegates every assigned
     * to this employee so you may want to check its active before using.
     */
    @Override
    public List<Delegation> findByDelegateEmpId(int delegateEmpId) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("delegateEmpId", delegateEmpId);
        String sql = SqlDelegateQuery.SELECT_BY_DELEGATE_EMP_ID.getSql(schemaMap());
        return localNamedJdbc.query(sql, params, new DelegateRowMapper(employeeInfoService));
    }

    private void deletePrincipalDelegations(int principalId) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("principalId", principalId);
        String sql = SqlDelegateQuery.DELETE_DELEGATIONS_FOR_PRINCIPAL.getSql(schemaMap());
        localNamedJdbc.update(sql, params);
    }

    private void insertDelegation(Delegation delegation) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("principalEmpId", delegation.principal.getEmployeeId())
                .addValue("delegateEmpId", delegation.delegate.getEmployeeId())
                .addValue("startDate", toDate(delegation.startDate))
                .addValue("endDate", toDate(delegation.endDate));
        String sql = SqlDelegateQuery.INSERT_DELEGATION.getSql(schemaMap());
        KeyHolder keyHolder = new GeneratedKeyHolder();
        localNamedJdbc.update(sql, params, keyHolder);
        delegation.id = (Integer) keyHolder.getKeys().get("delegation_id");
    }

    private enum SqlDelegateQuery implements BasicSqlQuery {
        SELECT_DELEGATES(
                "SELECT delegation_id, principal_emp_id, delegate_emp_id, start_date, end_date\n" +
                        " FROM ${travelSchema}.delegation\n" +
                        " WHERE principal_emp_id = :principalId"
        ),
        INSERT_DELEGATION(
                "INSERT INTO ${travelSchema}.delegation(principal_emp_id, delegate_emp_id, start_date, end_date)\n" +
                        " VALUES(:principalEmpId, :delegateEmpId, :startDate, :endDate)"
        ),
        DELETE_DELEGATIONS_FOR_PRINCIPAL(
                "DELETE FROM ${travelSchema}.delegation WHERE principal_emp_id = :principalId"
        ),
        SELECT_BY_DELEGATE_EMP_ID(
                "SELECT delegation_id, principal_emp_id, delegate_emp_id, start_date, end_date\n" +
                        " FROM ${travelSchema}.delegation\n" +
                        " WHERE delegate_emp_id = :delegateEmpId"
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

    private class DelegateRowMapper extends BaseRowMapper<Delegation> {

        private EmployeeInfoService employeeInfoService;

        public DelegateRowMapper(EmployeeInfoService employeeInfoService) {
            this.employeeInfoService = employeeInfoService;
        }

        @Override
        public Delegation mapRow(ResultSet rs, int i) throws SQLException {
            return new Delegation(
                    rs.getInt("delegation_id"),
                    employeeInfoService.getEmployee(rs.getInt("principal_emp_id")),
                    employeeInfoService.getEmployee(rs.getInt("delegate_emp_id")),
                    getLocalDateFromRs(rs, "start_date"),
                    getLocalDateFromRs(rs, "end_date")
            );
        }
    }
}
