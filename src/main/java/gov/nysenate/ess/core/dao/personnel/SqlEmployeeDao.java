package gov.nysenate.ess.core.dao.personnel;

import gov.nysenate.ess.core.dao.base.PaginatedRowHandler;
import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import gov.nysenate.ess.core.dao.personnel.mapper.EmployeeRowMapper;
import gov.nysenate.ess.core.dao.unit.LocationDao;
import gov.nysenate.ess.core.department.DepartmentDao;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.model.personnel.EmployeeException;
import gov.nysenate.ess.core.model.personnel.EmployeeNotFoundEx;
import gov.nysenate.ess.core.model.transaction.TransactionCode;
import gov.nysenate.ess.core.util.LimitOffset;
import gov.nysenate.ess.core.util.OrderBy;
import gov.nysenate.ess.core.util.PaginatedList;
import gov.nysenate.ess.core.util.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;

@Repository
public class SqlEmployeeDao extends SqlBaseDao implements EmployeeDao
{
    private static final Logger logger = LoggerFactory.getLogger(SqlEmployeeDao.class);

    @Autowired private LocationDao locationDao;
    @Autowired private DepartmentDao departmentDao;

    /** {@inheritDoc} */
    @Override
    public Employee getEmployeeById(int empId) throws EmployeeException {
        Employee employee;
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("empId", empId);
        try {
            employee = remoteNamedJdbc.queryForObject(SqlEmployeeQuery.GET_EMP_BY_ID_SQL.getSql(schemaMap()), params, getEmployeeRowMapper());
            setEmployeeDepartment(employee);
        }
        catch (DataRetrievalFailureException ex) {
            logger.warn("Retrieve employee {} error: {}", empId, ex.getMessage());
            throw new EmployeeNotFoundEx("No matching employee record for employee id: " + empId);
        }
        return employee;
    }

    /** {@inheritDoc} */
    @Override
    public Map<Integer, Employee> getEmployeesByIds(List<Integer> empIds) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("empIdSet", new HashSet<>(empIds));
        return getEmployeeMap(SqlEmployeeQuery.GET_EMPS_BY_IDS_SQL.getSql(schemaMap()), params);
    }

    /** {@inheritDoc} */
    @Override
    public Employee getEmployeeByEmail(String email) throws EmployeeException {
        Employee employee;
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("email", email);
        try {
            employee = remoteNamedJdbc.queryForObject(SqlEmployeeQuery.GET_EMP_BY_EMAIL_SQL.getSql(schemaMap()), params, getEmployeeRowMapper());
            setEmployeeDepartment(employee);
        }
        catch (DataRetrievalFailureException ex) {
            throw new EmployeeNotFoundEx("No matching employee record for email: " + email);
        }
        return employee;
    }

    /** {@inheritDoc} */
    @Override
    public Set<Employee> getAllEmployees() {
        Set<Employee> emps = new HashSet<>(remoteNamedJdbc.query(
                SqlEmployeeQuery.GET_ALL_EMPS_SQL.getSql(schemaMap()), getEmployeeRowMapper()));
        for (Employee emp: emps) {
            setEmployeeDepartment(emp);
        }
        return emps;
    }

    /** {@inheritDoc} */
    @Override
    public Set<Employee> getActiveEmployees() {
        Set<Employee> emps = new HashSet<>(remoteNamedJdbc.query(
                SqlEmployeeQuery.GET_ACTIVE_EMPS_SQL.getSql(schemaMap()), getEmployeeRowMapper()));
        for (Employee emp: emps) {
            setEmployeeDepartment(emp);
        }
        return emps;
    }

    /** {@inheritDoc} */
    @Override
    public PaginatedList<Employee> searchEmployees(String term, boolean activeOnly, LimitOffset limitOffset) {
        SqlParameterSource params = new MapSqlParameterSource("term", term)
                .addValue("activeOnly", activeOnly);
        OrderBy orderBy = new OrderBy(
                "per.FFNALAST", SortOrder.ASC,
                "per.FFNAFIRST", SortOrder.ASC,
                "per.FFNAMIDINIT", SortOrder.ASC
        );
        PaginatedRowHandler<Employee> rowHandler =
                new PaginatedRowHandler<>(limitOffset, "total_rows", getEmployeeRowMapper());
        final String searchDml = SqlEmployeeQuery.GET_EMPS_BY_SEARCH_TERM.getSql(schemaMap(), orderBy, limitOffset);
        remoteNamedJdbc.query(searchDml, params, rowHandler);
        for (Employee emp : rowHandler.getList().getResults()) {
            setEmployeeDepartment(emp);
        }
        return rowHandler.getList();
    }

    /** {@inheritDoc} */
    @Override
    public Set<Integer> getActiveEmployeeIds(){
        return new HashSet<>(remoteNamedJdbc.query(SqlEmployeeQuery.GET_ACTIVE_EMP_IDS.getSql(schemaMap()),
                (rs, rowNum) -> rs.getInt("NUXREFEM")));
    }

    @Override
    public Map<String, String> getRawEmployeeColumns(int empId) {
        final String getRawEmpColsSql = SqlEmployeeQuery.GET_EMP_BY_ID_SQL.getSql(
                schemaMap(), new OrderBy("DTTXNUPDATE", SortOrder.DESC), LimitOffset.ONE);
        return remoteNamedJdbc.queryForObject(getRawEmpColsSql, new MapSqlParameterSource("empId", empId),
                (rs, rowNum) -> {
                    Map<String, String> employeeColumns = new HashMap<>();
                    for (String colName : TransactionCode.getAllDbColumnsList()) {
                        try {
                            employeeColumns.put(colName, rs.getString(colName));
                        } catch (SQLException ex) {
                            employeeColumns.put(colName, null);
                        }
                    }
                    return employeeColumns;
                }
        );
    }

    @Override
    public LocalDateTime getLastUpdateTime() {
        return remoteNamedJdbc.queryForObject(SqlEmployeeQuery.GET_LATEST_UPDATE_DATE.getSql(schemaMap()), new MapSqlParameterSource(),
                (rs, rowNum) -> getLocalDateTime(rs, "MAX_UPDATE_DATE"));
    }

    @Override
    public List<Employee> getUpdatedEmployees(LocalDateTime fromDateTime) {
        List<Employee> emps = remoteNamedJdbc.query(SqlEmployeeQuery.GET_EMP_BY_UPDATE_DATE.getSql(schemaMap()),
                new MapSqlParameterSource("lastUpdate", toDate(fromDateTime)), getEmployeeRowMapper());
        for (Employee emp : emps) {
            setEmployeeDepartment(emp);
        }
        return emps;
    }

    /**
     * Helper method to create employee id -> Employee object mappings.
     * @param sql String - The sql query to execute
     * @param params MapSqlParameterSource - The parameters to supply to the sql query.
     * @return Map(Integer, Employee)
     */
    private Map<Integer, Employee> getEmployeeMap(String sql, MapSqlParameterSource params) {
        Map<Integer, Employee> employeeMap = new LinkedHashMap<>();
        List<Employee> employees = remoteNamedJdbc.query(sql, params, getEmployeeRowMapper());
        for (Employee emp : employees) {
            employeeMap.put(emp.getEmployeeId(), emp);
            setEmployeeDepartment(emp);
        }
        return employeeMap;
    }

    private void setEmployeeDepartment(Employee emp) {
        emp.setDepartment(departmentDao.getEmployeeDepartment(emp.getEmployeeId()));
    }

    /** Returns a EmployeeRowMapper that's configured for use in this dao */
    private EmployeeRowMapper getEmployeeRowMapper() {
        return new EmployeeRowMapper("", "RCTR_", "RCTRHD_", "AGCY_", "LOC_", locationDao);
    }
}
