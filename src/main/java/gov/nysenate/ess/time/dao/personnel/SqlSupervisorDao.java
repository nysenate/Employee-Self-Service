package gov.nysenate.ess.time.dao.personnel;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Range;
import com.google.common.collect.Table;
import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import gov.nysenate.ess.core.dao.transaction.SqlEmpTransactionDao;
import gov.nysenate.ess.core.dao.transaction.mapper.TransInfoRowMapper;
import gov.nysenate.ess.core.model.personnel.PersonnelStatus;
import gov.nysenate.ess.core.model.transaction.TransactionInfo;
import gov.nysenate.ess.core.model.transaction.TransactionCode;
import gov.nysenate.ess.time.model.personnel.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static gov.nysenate.ess.core.util.DateUtils.endOfDateRange;
import static gov.nysenate.ess.core.util.DateUtils.startOfDateRange;
import static gov.nysenate.ess.core.model.transaction.TransactionCode.*;

@Repository
public class SqlSupervisorDao extends SqlBaseDao implements SupervisorDao
{
    private static final Logger logger = LoggerFactory.getLogger(SqlSupervisorDao.class);

    @Autowired private SqlEmpTransactionDao empTransDao;

    private final Set<TransactionCode> supTransCodes = new HashSet<>(Arrays.asList(ACC, SUP,APP,RTP));
    private final Set<TransactionCode> checkedTransCodes = new HashSet<>(Arrays.asList(EMP,SUP,APP,RTP));

    /**
     * {@inheritDoc}
     * We determine this by simply checking if the empId managed any employees during the
     * given time range.
     */
    @Override
    public boolean isSupervisor(int empId, Range<LocalDate> dateRange) {
        SupervisorEmpGroup supervisorEmpGroup;
        try {
            supervisorEmpGroup = getSupervisorEmpGroup(empId, dateRange);
        }
        catch (SupervisorException ex) {
            return false;
        }
        return supervisorEmpGroup.hasEmployees();
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
    public SupervisorEmpGroup getSupervisorEmpGroup(int supId, Range<LocalDate> dateRange) throws SupervisorException {
        MapSqlParameterSource params = new MapSqlParameterSource();
        LocalDate startDate = startOfDateRange(dateRange);
        LocalDate endDate = endOfDateRange(dateRange);
        params.addValue("supId", supId);
        params.addValue("endDate", toDate(endDate));
        List<Map<String, Object>> res;
        try {
            res = remoteNamedJdbc.query(SqlSupervisorQuery.GET_SUP_EMP_TRANS_SQL.getSql(schemaMap()), params, new ColumnMapRowMapper());
        }
        catch (DataRetrievalFailureException ex) {
            throw new SupervisorException("Failed to retrieve matching employees for supId: " + supId + " before: " + endDate);
        }

        /**
         * The transactions for the matching employees need to be processed to determine if they
         * are still under the given supervisor.
         */
        if (!res.isEmpty()) {
            SupervisorEmpGroup empGroup = new SupervisorEmpGroup(supId, startDate, endDate);
            Map<Integer, EmployeeSupInfo> primaryEmps = new HashMap<>();
            Map<Integer, EmployeeSupInfo> overrideEmps = new HashMap<>();
            Table<Integer, Integer, EmployeeSupInfo> supOverrideEmps = HashBasedTable.create();
            Map<Integer, LocalDate> possiblePrimaryEmps = new HashMap<>();
            Table<Integer, Integer, LocalDate> possibleSupOvrEmps = HashBasedTable.create();

            for (Map<String,Object> colMap : res) {
                logger.trace(colMap.toString());
                String group = colMap.get("EMP_GROUP").toString();
                int empId = Integer.parseInt(colMap.get("NUXREFEM").toString());
                TransactionCode transType = TransactionCode.valueOf(colMap.get("CDTRANS").toString());
                PersonnelStatus perStatus = PersonnelStatus.valueOf(colMap.get("CDSTATPER").toString());
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");
                LocalDate effectDate = LocalDate.from(formatter.parse(colMap.get("DTEFFECT").toString()));
                int rank = Integer.parseInt(colMap.get("TRANS_RANK").toString());
                boolean empTerminated = false;
                boolean effectDateIsPast = effectDate.compareTo(startDate) <= 0;

                if (colMap.get("NUXREFSV") == null || !StringUtils.isNumeric(colMap.get("NUXREFSV").toString())) {
                    continue;
                }

                int currSupId = Integer.parseInt(colMap.get("NUXREFSV").toString());

                EmployeeSupInfo empSupInfo = new EmployeeSupInfo(empId, currSupId, startDate, endDate);
                empSupInfo.setEmpLastName(colMap.get("NALAST").toString());
                if (transType.equals(EMP)) {
                    empTerminated = true;

                    // If the employee is retiring, the effective retirement will start the next day
                    if (perStatus == PersonnelStatus.RETD) {
                        effectDate = effectDate.plusDays(1);
                    }
                    empSupInfo.setSupEndDate(effectDate);
                }
                else {
                    empSupInfo.setSupStartDate(effectDate);
                }

                /**
                 * The first rank record for a given empId contains latest transaction that took effect
                 * before/on the given 'end' date.
                 */
                if (rank == 1) {
                    /**
                     * Add the employee to their supervisor's respective group if their supervisor id
                     * matches the given 'supId'. For PRIMARY AND SUP_OVR codes we flag mismatches as possible
                     * employees when the effect date is between the 'start' and 'end' dates. The proceeding
                     * record(s) for those employees will then need to be checked to see if the supervisor matches
                     * at some point on/after the 'start' date.
                     */
                    switch (group) {
                        case "PRIMARY": {
                            if (currSupId == supId && !empTerminated) {
                                primaryEmps.put(empId, empSupInfo);
                            }
                            else if (!effectDateIsPast) {
                                possiblePrimaryEmps.put(empId, effectDate);
                            }
                            break;
                        }
                        case "EMP_OVR": {
                            if (empTerminated && effectDateIsPast) {
                                continue;
                            }
                            overrideEmps.put(empId, empSupInfo);
                            break;
                        }
                        case "SUP_OVR": {
                            int ovrSupId = Integer.parseInt(colMap.get("OVR_NUXREFSV").toString());
                            if (currSupId == ovrSupId && !empTerminated) {
                                supOverrideEmps.put(ovrSupId, empId, empSupInfo);
                            }
                            else if (!effectDateIsPast) {
                                possibleSupOvrEmps.put(ovrSupId, empId, effectDate);
                            }
                            break;
                        }
                    }
                }
                else {
                    /**
                     * Process the records of employees that had a supervisor change during the date range.
                     * If a supervisor match is found to occur on/before the 'start' date, we addUsage them to their
                     * respective supervisor group. Otherwise if we can't find a match and the effect date has
                     * occurred before the 'start' date, we know that they don't belong in the group for this range.
                     */
                    switch (group) {
                        case "PRIMARY": {
                            if (possiblePrimaryEmps.containsKey(empId)) {
                                if (currSupId == supId && !empTerminated) {
                                    empSupInfo.setSupEndDate(possiblePrimaryEmps.get(empId));
                                    primaryEmps.put(empId, empSupInfo);
                                }
                                else if (!effectDateIsPast) {
                                    possiblePrimaryEmps.put(empId, effectDate);
                                }
                                else {
                                    possiblePrimaryEmps.remove(empId);
                                }
                            }
                            break;
                        }
                        case "SUP_OVR": {
                            int ovrSupId = Integer.parseInt(colMap.get("OVR_NUXREFSV").toString());
                            if (possibleSupOvrEmps.contains(ovrSupId, empId)) {
                                if (currSupId == ovrSupId && !empTerminated) {
                                    empSupInfo.setSupEndDate(possibleSupOvrEmps.get(ovrSupId, empId));
                                    supOverrideEmps.put(ovrSupId, empId, empSupInfo);
                                }
                                else if (!effectDateIsPast) {
                                    possibleSupOvrEmps.put(ovrSupId, empId, effectDate);
                                }
                                else {
                                    possibleSupOvrEmps.remove(ovrSupId, empId);
                                }
                            }
                            break;
                        }
                    }
                }
            }

            empGroup.setPrimaryEmployees(primaryEmps);
            empGroup.setOverrideEmployees(overrideEmps);
            empGroup.setSupOverrideEmployees(supOverrideEmps);
            return empGroup;
        }
        throw new SupervisorMissingEmpsEx("No employee associations could be found for supId: " + supId + " before " + endDate);
    }

    /**{@inheritDoc} */
    @Override
    public List<SupervisorOverride> getSupervisorOverrides(int supId, SupGrantType type) throws SupervisorException {
        SqlParameterSource params = new MapSqlParameterSource("empId", supId);
        String sql = (type.equals(SupGrantType.GRANTEE)) ? SqlSupervisorQuery.GET_SUP_OVERRIDES.getSql(schemaMap())
                                                         : SqlSupervisorQuery.GET_SUP_GRANTS.getSql(schemaMap());
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
        return remoteNamedJdbc.queryForObject(SqlSupervisorQuery.GET_LATEST_SUP_UPDATE_DATE.getSql(schemaMap()),
                new MapSqlParameterSource(), (rs, rowNum) -> getLocalDateTime(rs, "DTTXNUPDATE"));
    }
}
