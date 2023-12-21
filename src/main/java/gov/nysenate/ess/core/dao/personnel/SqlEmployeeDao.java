package gov.nysenate.ess.core.dao.personnel;

import gov.nysenate.ess.core.dao.base.PaginatedRowHandler;
import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import gov.nysenate.ess.core.dao.personnel.mapper.EmployeeRowMapper;
import gov.nysenate.ess.core.dao.personnel.mapper.MinimalEmployeeRowMapper;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.model.personnel.EmployeeException;
import gov.nysenate.ess.core.model.personnel.EmployeeNotFoundEx;
import gov.nysenate.ess.core.model.transaction.TransactionCode;
import gov.nysenate.ess.core.service.base.LocationService;
import gov.nysenate.ess.core.service.personnel.EmployeeSearchBuilder;
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
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.*;

@Repository
public class SqlEmployeeDao extends SqlBaseDao implements EmployeeDao {
    private static final Logger logger = LoggerFactory.getLogger(SqlEmployeeDao.class);

    private final LocationService locationService;

    @Autowired
    public SqlEmployeeDao(LocationService locationService) {
        this.locationService = locationService;
    }

    /** {@inheritDoc} */
    @Override
    public Employee getEmployeeById(int empId) throws EmployeeException {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("empId", empId);
        try {
            List<Employee> employeeList = remoteNamedJdbc.query(SqlEmployeeQuery.GET_EMP_BY_ID_SQL.getSql(schemaMap()), params, getEmployeeRowMapper());
            if (employeeList.isEmpty()) {
                throw new DataRetrievalFailureException("No matching employee found.");
            }
            return employeeList.get(0);
        }
        catch (DataRetrievalFailureException ex) {
            logger.warn("Retrieve employee {} error: {}", empId, ex.getMessage());
            throw new EmployeeNotFoundEx("No matching employee record for employee id: " + empId);
        }
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
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("email", email);
        try {
            List<Employee> employees = remoteNamedJdbc.query(SqlEmployeeQuery.GET_EMP_BY_EMAIL_SQL.getSql(schemaMap()), params, getEmployeeRowMapper());
            if (employees.isEmpty())  {
                throw new DataRetrievalFailureException("");
            }
            return employees.get(0);
        }
        catch (DataRetrievalFailureException ex) {
            throw new EmployeeNotFoundEx("No matching employee record for email: " + email);
        }
    }

    /** {@inheritDoc} */
    @Override
    public Set<Employee> getAllEmployees() {
        return new HashSet<>(remoteNamedJdbc.query(
                SqlEmployeeQuery.GET_ALL_EMPS_SQL.getSql(schemaMap()), getEmployeeRowMapper()));
    }

    /** {@inheritDoc} */
    @Override
    public Set<Employee> getActiveEmployees() {
        return new HashSet<>(remoteNamedJdbc.query(
                SqlEmployeeQuery.GET_ACTIVE_EMPS_SQL.getSql(schemaMap()), getEmployeeRowMapper()));
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
        return rowHandler.getList();
    }

    @Override
    public PaginatedList<Employee> searchEmployees(EmployeeSearchBuilder employeeSearchBuilder, LimitOffset limitOffset) {
        MapSqlParameterSource params = getEmpSearchParams(employeeSearchBuilder);
        OrderBy orderBy = new OrderBy(
                "per.FFNALAST", SortOrder.ASC,
                "per.FFNAFIRST", SortOrder.ASC,
                "per.FFNAMIDINIT", SortOrder.ASC
        );
        PaginatedRowHandler<Employee> rowHandler =
                new PaginatedRowHandler<>(limitOffset, "total_rows", getEmployeeRowMapper());
        final String searchDml = SqlEmployeeQuery.GET_EMPS_BY_SEARCH_QUERY.getSql(schemaMap(), orderBy, limitOffset);
        remoteNamedJdbc.query(searchDml, params, rowHandler);
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
        Map<String, String> employeeColumns = new HashMap<>();
        remoteNamedJdbc.query(getRawEmpColsSql, new MapSqlParameterSource("empId", empId),
                (rs, rowNum) -> {
                    for (String colName : TransactionCode.getAllDbColumnsList()) {
                        String col = null;
                        try {
                            col = rs.getString(colName);
                        } catch (SQLException ignored) {}
                        employeeColumns.put(colName, col);
                    }
                    return employeeColumns;
                }
        );
        return employeeColumns;
    }

    @Override
    public LocalDateTime getLastUpdateTime() {
        List<LocalDateTime> timestamps = remoteNamedJdbc.query(SqlEmployeeQuery.GET_LATEST_UPDATE_DATE.getSql(schemaMap()), new MapSqlParameterSource(),
                (rs, rowNum) -> getLocalDateTime(rs, "MAX_UPDATE_DATE"));

        if (timestamps.isEmpty()) {
            return null;
        }
        else {
            return timestamps.get(0);
        }
    }

    @Override
    public List<Employee> getUpdatedEmployees(LocalDateTime fromDateTime) {
        return remoteNamedJdbc.query(SqlEmployeeQuery.GET_EMP_BY_UPDATE_DATE.getSql(schemaMap()),
                new MapSqlParameterSource("lastUpdate", toDate(fromDateTime)), getEmployeeRowMapper());
    }

    @Override
    public List<Employee> getInactivatedEmployeesSinceDate(LocalDateTime since) {
        DateTimeFormatter formatter= new DateTimeFormatterBuilder().parseCaseSensitive()
                .appendPattern("dd-MMM-yyyy").toFormatter();
        String formattedDate = since.format(formatter).toUpperCase();
        return remoteNamedJdbc.query(SqlEmployeeQuery.GET_INACTIVE_EMPLOYEES_SINCE_DATE.getSql(schemaMap()),
                new MapSqlParameterSource("since", formattedDate), getMinimalEmployeeRowMapper());
    }

    @Override
    public List<Employee> getNewEmployees() {
        return remoteJdbc.query(SqlEmployeeQuery.GET_NEW_EMPLOYEES.getSql(schemaMap()), getMinimalEmployeeRowMapper());
    }

    private MapSqlParameterSource getEmpSearchParams(EmployeeSearchBuilder searchBuilder) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("name", Optional.ofNullable(searchBuilder.getName())
                .map(name -> name.trim()
                        .toUpperCase()
                        .replaceAll("[^A-Z ]", "")
                        .replaceAll(" +", " "))
                .orElse(null));
        params.addValue("empStatus",
                Optional.ofNullable(searchBuilder.getActive())
                        .map(SqlBaseDao::getStatusCode)
                        .map(String::valueOf)
                        .orElse(null));
        params.addValue("respCtrHeadCodesEmpty", searchBuilder.getRespCtrHeadCodes().isEmpty());
        params.addValue("respCtrHeadCodes",
                searchBuilder.getRespCtrHeadCodes().isEmpty() ? null : searchBuilder.getRespCtrHeadCodes());
        params.addValue("contServFrom", toDate(searchBuilder.getContinuousServiceFrom()));
        params.addValue("contServTo", toDate(searchBuilder.getContinuousServiceTo()));
        return params;
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
        }
        return employeeMap;
    }

    /** Returns a EmployeeRowMapper that's configured for use in this dao */
    private EmployeeRowMapper getEmployeeRowMapper() {
        return new EmployeeRowMapper("", "RCTR_", "RCTRHD_", "AGCY_", "LOC_", locationService);
    }

    private MinimalEmployeeRowMapper getMinimalEmployeeRowMapper() {
        return new MinimalEmployeeRowMapper("");
    }
}
