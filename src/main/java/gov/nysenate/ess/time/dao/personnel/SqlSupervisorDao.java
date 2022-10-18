package gov.nysenate.ess.time.dao.personnel;

import com.google.common.collect.HashMultimap;
import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import gov.nysenate.ess.core.dao.transaction.mapper.TransInfoRowMapper;
import gov.nysenate.ess.core.model.payroll.PayType;
import gov.nysenate.ess.core.model.personnel.Agency;
import gov.nysenate.ess.core.model.personnel.PersonnelStatus;
import gov.nysenate.ess.core.model.transaction.TransactionCode;
import gov.nysenate.ess.core.model.transaction.TransactionInfo;
import gov.nysenate.ess.core.util.DateUtils;
import gov.nysenate.ess.time.dao.personnel.mapper.SupervisorOverrideRowMapper;
import gov.nysenate.ess.time.model.personnel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static gov.nysenate.ess.core.model.transaction.TransactionCode.EMP;

@Repository
public class SqlSupervisorDao extends SqlBaseDao implements SupervisorDao
{
    private static final Logger logger = LoggerFactory.getLogger(SqlSupervisorDao.class);

    /**{@inheritDoc} */
    @Override
    public boolean isSupervisor(int empId) {
        MapSqlParameterSource params = new MapSqlParameterSource("empId", empId);
        try {
            List<Object> booleans = remoteNamedJdbc.query(SqlSupervisorQuery.TEST_IF_SUPERVISOR.getSql(schemaMap()),
                    params, (rs, rowNum) -> rs.getString("1"));

            if (booleans.isEmpty()) {
                return false;
            }
            else {
                // TODO: "booleans" is really a list of Strings, so this fails. Note that the Strings are not just "false" or "true".
                return (Boolean) booleans.get(0);
            }
        } catch (EmptyResultDataAccessException ex) {
            return false;
        }
    }

    /**{@inheritDoc} */
    @Override
    public SupervisorChainAlteration getSupervisorChainAlterations(int empId) {
        SqlParameterSource params = new MapSqlParameterSource("empId", empId);
        Set<Integer> inclusions = new HashSet<>();
        Set<Integer> exclusions = new HashSet<>();
        List<Map<String, Object>> res = remoteNamedJdbc.query(
            SqlSupervisorQuery.GET_SUP_CHAIN_EXCEPTIONS.getSql(schemaMap()), params, new ColumnMapRowMapper());
        if (!res.isEmpty()) {
            for (Map<String, Object> row : res) {
                int supId = Integer.parseInt(row.get("NUXREFSV").toString());
                if (row.get("CDTYPE").equals("I")) {
                    inclusions.add(supId);
                }
                else {
                    exclusions.add(supId);
                }
            }
        }
        return new SupervisorChainAlteration(inclusions, exclusions);
    }

    /**{@inheritDoc} */
    @Override
    public PrimarySupEmpGroup getPrimarySupEmpGroup(int supId) throws SupervisorException {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("supId", supId);
        List<Map<String, Object>> res;
        try {
            res = remoteNamedJdbc.query(SqlSupervisorQuery.GET_SUP_EMP_TRANS_SQL.getSql(schemaMap()), params, new ColumnMapRowMapper());
        }
        catch (DataRetrievalFailureException ex) {
            throw new SupervisorException("Failed to retrieve matching employees for supId: " + supId, ex);
        }

        if (res.isEmpty()) {
            throw new SupervisorMissingEmpsEx(supId);
        }

        /*
         * The transactions for the matching employees need to be processed to determine if they
         * are still under the given supervisor.
         */
        HashMultimap<Integer, EmployeeSupInfo> primaryEmps = HashMultimap.create();

        Map<Integer, LocalDate> possiblePrimaryEmps = new HashMap<>();

        for (Map<String,Object> colMap : res) {

            EmployeeSupInfo empSupInfo;
            try {
                empSupInfo = extractEmployeeSupInfo(colMap);
            } catch (EmpSupInfoNullValEx ex) {
                // If there is a null value for an employee, set a warning message and ignore the employee.
                // This is expected, as employee data is set gradually during the appoint process
                if (!"NUXREFSV".equals(ex.field)) {
                    logger.warn(ex.getMessage());
                }
                continue;
            }

            int empId = empSupInfo.getEmpId();
            int currSupId = empSupInfo.getSupId();
            int rank = Integer.parseInt(colMap.get("TRANS_RANK").toString());
            LocalDate effectDate = getDateCol(colMap, "DTEFFECT");

            TransactionCode transType = TransactionCode.valueOf(colMap.get("CDTRANS").toString());

            boolean empTerminated = false;

            if (transType.equals(EMP)) {
                empTerminated = true;

                PersonnelStatus perStatus = PersonnelStatus.valueOf(colMap.get("CDSTATPER").toString());
                // Offset the effect date depending on the personnel status
                effectDate = effectDate.plusDays(perStatus.getEffectDateOffset());
                empSupInfo.setSupEndDate(effectDate);
            }
            else {
                empSupInfo.setSupStartDate(effectDate);
            }

            /*
             * The first rank record for a given empId contains latest transaction that took effect
             * before/on the given 'end' date.
             */
            if (rank == 1) {
                /*
                 * Add the employee to their supervisor's respective group if:
                 *  - the employee is not the supervisor
                 *  - their supervisor id matches the given 'supId'
                 *  - the employee is not currently terminated
                 */
                if (empId != supId && currSupId == supId && !empTerminated) {
                    addSupEmpGroup(primaryEmps, empSupInfo);
                }
                possiblePrimaryEmps.put(empId, effectDate);
            }
            else {
                /*
                 * Process the records of employees that had a supervisor change during the date range.
                 * If a supervisor match is found to occur on/before the 'start' date, we add them to their
                 * respective supervisor group. Otherwise if we can't find a match and the effect date has
                 * occurred before the 'start' date, we know that they don't belong in the group for this range.
                 */
                if (possiblePrimaryEmps.containsKey(empId)) {
                    if (currSupId == supId && !empTerminated) {
                        empSupInfo.setSupEndDate(possiblePrimaryEmps.get(empId));

                        // Add the employee only if it is not the supervisor,
                        // and the effective date range is non-empty
                        if (!(empId == supId || empSupInfo.getEffectiveDateRange().isEmpty())) {
                            addSupEmpGroup(primaryEmps, empSupInfo);
                        }
                        possiblePrimaryEmps.remove(empId);
                    }
                    possiblePrimaryEmps.put(empId, effectDate);
                }
                // Indicates that the supervisor had this employee for
                // at least one additional period of time
                // In this case add another possible primary Emp
                else if (primaryEmps.containsKey(empId) &&
                        (empTerminated || currSupId != supId)) {
                    possiblePrimaryEmps.put(empId, effectDate);
                }
            }
        }

        PrimarySupEmpGroup empGroup = new PrimarySupEmpGroup(supId);
        empGroup.setPrimaryEmployees(primaryEmps);
        return empGroup;
    }

    /**{@inheritDoc} */
    @Override
    public List<SupervisorOverride> getAllOverrides(int supId) throws SupervisorException {
        SqlParameterSource params = new MapSqlParameterSource("empId", supId);
        final String sql = SqlSupervisorQuery.GET_ALL_OVERRIDES.getSql(schemaMap());
        return remoteNamedJdbc.query(sql, params, new SupervisorOverrideRowMapper());
    }

    /**{@inheritDoc} */
    @Override
    public List<SupervisorOverride> getSupervisorOverrides(int supId) throws SupervisorException {
        SqlParameterSource params = new MapSqlParameterSource("empId", supId);
        final String sql = SqlSupervisorQuery.GET_SUP_OVERRIDES.getSql(schemaMap());
        return remoteNamedJdbc.query(sql, params, new SupervisorOverrideRowMapper());
    }

    /**{@inheritDoc} */
    @Override
    public List<SupervisorOverride> getSupervisorGrants(int supId) throws SupervisorException {
        SqlParameterSource params = new MapSqlParameterSource("empId", supId);
        final String sql = SqlSupervisorQuery.GET_SUP_GRANTS.getSql(schemaMap());
        return remoteNamedJdbc.query(sql, params, new SupervisorOverrideRowMapper());
    }

    /**{@inheritDoc} */
    @Override
    public void setSupervisorOverride(int granterSupId, int granteeSupId, boolean active, LocalDate startDate, LocalDate endDate) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("granterSupId", granterSupId);
        params.addValue("granteeSupId", granteeSupId);
        params.addValue("status", (active) ? "A" : "I");
        params.addValue("startDate", toDate(startDate));
        params.addValue("endDate", toDate(endDate));
        if (remoteNamedJdbc.update(SqlSupervisorQuery.UPDATE_SUP_GRANT.getSql(schemaMap()), params) == 0) {
            remoteNamedJdbc.update(SqlSupervisorQuery.INSERT_SUP_GRANT.getSql(schemaMap()), params);
        }
    }

    @Override
    public List<TransactionInfo> getSupTransChanges(LocalDateTime fromDateTime) {
        MapSqlParameterSource params = new MapSqlParameterSource("fromDate", toDate(fromDateTime));
        return remoteNamedJdbc.query(SqlSupervisorQuery.GET_SUP_EMP_TRANS_UPDATED_SINCE.getSql(schemaMap()),
                params, new TransInfoRowMapper());
    }

    @Override
    public List<SupervisorOverride> getSupOverrideChanges(LocalDateTime fromDateTime) {
        MapSqlParameterSource params = new MapSqlParameterSource("fromDate", toDate(fromDateTime));
        return remoteNamedJdbc.query(SqlSupervisorQuery.GET_SUP_OVR_IDS_UPDATED_SINCE.getSql(schemaMap()),
                params, new SupervisorOverrideRowMapper());
    }

    @Override
    public LocalDateTime getLastSupUpdateDate() {
        List<Object> timestamps = remoteNamedJdbc.query(SqlSupervisorQuery.GET_LATEST_SUP_UPDATE_DATE.getSql(schemaMap()),
                new MapSqlParameterSource(), (rs, rowNum) -> getLocalDateTime(rs, "DTTXNUPDATE"));

        if (timestamps.isEmpty()) {
            return null;
        }
        else {
            return (LocalDateTime) timestamps.get(0);
        }
    }

    /* --- Internal Methods --- */

    private LocalDate getDateCol(Map<String, Object> colMap, String colName) {
        return Optional.ofNullable(colMap.get(colName))
                .map(Object::toString)
                .map(DateUtils.SFMS_DATE_TIME_FMT::parse)
                .map(LocalDate::from)
                .orElse(null);
    }

    /**
     * Adds a {@link EmployeeSupInfo} to the employee map.
     * Looks for {@link EmployeeSupInfo} that are equal with intersecting dates and merges them.
     */
    private static void addSupEmpGroup(HashMultimap<Integer, EmployeeSupInfo> map, EmployeeSupInfo employeeSupInfo) {
        // If there are existing emp sup infos, see if they can be merged
        if (map.containsKey(employeeSupInfo.getEmpId())) {
            Set<EmployeeSupInfo> employeeSupInfos = map.get(employeeSupInfo.getEmpId());
            for (Iterator<EmployeeSupInfo> itr = employeeSupInfos.iterator(); itr.hasNext(); ) {
                EmployeeSupInfo existingEsi = itr.next();
                if (employeeSupInfo.isConnected(existingEsi)) {
                    employeeSupInfo = employeeSupInfo.merge(existingEsi);
                    itr.remove();
                }
            }
        }
        map.put(employeeSupInfo.getEmpId(), employeeSupInfo);
    }

    /**
     * Generates an {@link EmployeeSupInfo} from a supervisor query result set.
     */
    private EmployeeSupInfo extractEmployeeSupInfo(Map<String, Object> colMap) throws EmpSupInfoNullValEx {
        int empId = Integer.parseInt(colMap.get("NUXREFEM").toString());

        int currSupId = Integer.parseInt(getColVal(empId, "NUXREFSV", colMap));

        EmployeeSupInfo empSupInfo = new EmployeeSupInfo(empId, currSupId);

        empSupInfo.setEmpLastName(getColVal(empId, "FFNALAST", colMap));
        empSupInfo.setEmpFirstName(getColVal(empId, "FFNAFIRST", colMap));

        String agencyCode = getColVal(empId, "CDAGENCY", colMap);
        empSupInfo.setSenator(Agency.SENATOR_AGENCY_CODE.equals(agencyCode));

        String payTypeVal = getColVal(empId, "CDPAYTYPE", colMap);
        empSupInfo.setPayType(PayType.valueOf(payTypeVal));

        return empSupInfo;
    }

    /**
     * Attempts to extract a value from the supervisor query result map.
     * If the value is null, an exception is thrown.
     */
    private String getColVal(int empId, String field, Map<String, Object> colMap) throws EmpSupInfoNullValEx {
        Object value = colMap.get(field);
        if (value == null) {
            throw new EmpSupInfoNullValEx(empId, field);
        }
        return value.toString();
    }

    private class EmpSupInfoNullValEx extends Exception {
        private final int empId;
        private final String field;

        EmpSupInfoNullValEx(int empId, String field) {
            super("Employee #" + empId + " has null value for " + field);
            this.empId = empId;
            this.field = field;
        }
    }

}
