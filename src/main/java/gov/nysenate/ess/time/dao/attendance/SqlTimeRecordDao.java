package gov.nysenate.ess.time.dao.attendance;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Range;
import gov.nysenate.ess.core.util.DateUtils;
import gov.nysenate.ess.core.util.OrderBy;
import gov.nysenate.ess.core.util.SortOrder;
import gov.nysenate.ess.time.dao.attendance.mapper.RemoteEntryRowMapper;
import gov.nysenate.ess.time.dao.attendance.mapper.RemoteRecordRowMapper;
import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import gov.nysenate.ess.core.dao.period.mapper.PayPeriodRowMapper;
import gov.nysenate.ess.time.model.attendance.TimeEntry;
import gov.nysenate.ess.time.model.attendance.TimeRecord;
import gov.nysenate.ess.time.model.attendance.TimeRecordStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class SqlTimeRecordDao extends SqlBaseDao implements TimeRecordDao
{
    private static final Logger logger = LoggerFactory.getLogger(SqlTimeRecordDao.class);

    @Autowired private TimeEntryDao timeEntryDao;

    private static final OrderBy timeRecordOrder =
            new OrderBy("rec.NUXREFEM", SortOrder.ASC, "rec.DTBEGIN", SortOrder.ASC, "ent.DTDAY", SortOrder.ASC);

    /** {@inheritDoc} */
    @Override
    public TimeRecord getTimeRecord(BigInteger timeRecordId) throws EmptyResultDataAccessException {
        MapSqlParameterSource params = new MapSqlParameterSource("timesheetId", String.valueOf(timeRecordId));
        TimeRecordRowCallbackHandler rowHandler = new TimeRecordRowCallbackHandler();
        remoteNamedJdbc.query(SqlTimeRecordQuery.GET_TIME_REC_BY_ID.getSql(schemaMap()), params, rowHandler);
        List<TimeRecord> records = rowHandler.getRecordList();
        if (records.isEmpty()) {
            throw new EmptyResultDataAccessException("could not find time record with id: " + timeRecordId, 1);
        }
        return records.get(0);
    }

    /** {@inheritDoc} */
    @Override
    public ListMultimap<Integer, TimeRecord> getRecordsDuring(Set<Integer> empIds, Range<LocalDate> dateRange,
                                                              Set<TimeRecordStatus> statuses) {
        Set<String> statusCodes = statuses.stream()
                .map(TimeRecordStatus::getCode)
                .collect(Collectors.toSet());
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("empIds", empIds);
        params.addValue("startDate", toDate(DateUtils.startOfDateRange(dateRange)));
        params.addValue("endDate", toDate(DateUtils.endOfDateRange(dateRange)));
        params.addValue("statuses", statusCodes);
        TimeRecordRowCallbackHandler handler = new TimeRecordRowCallbackHandler();
        remoteNamedJdbc.query(
                SqlTimeRecordQuery.GET_TIME_REC_BY_DATES_EMP_ID.getSql(schemaMap(), timeRecordOrder), params, handler);
        return handler.getRecordMap();
    }

    /** {@inheritDoc} */
    @Override
    public LocalDateTime getLatestUpdateTime() {
        return DateUtils.getLocalDateTime(
                remoteJdbc.queryForObject(SqlTimeRecordQuery.GET_LAST_UPDATE_DATE_TIME.getSql(schemaMap()),
                        null, Timestamp.class));
    }

    /** {@inheritDoc} */
    @Override
    public List<TimeRecord> getUpdatedRecords(Range<LocalDateTime> dateTimeRange) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("startDateTime", toDate(DateUtils.startOfDateTimeRange(dateTimeRange)));
        params.addValue("endDateTime", toDate(DateUtils.endOfDateTimeRange(dateTimeRange)));
        TimeRecordRowCallbackHandler handler = new TimeRecordRowCallbackHandler();
        remoteNamedJdbc.query(
                SqlTimeRecordQuery.GET_UPDATED_TIME_RECS.getSql(schemaMap(), timeRecordOrder), params, handler);
        return handler.getRecordList();
    }

    /** {@inheritDoc} */
    @Override
    public List<TimeRecord> getActiveRecords(Integer empId) {
        TimeRecordRowCallbackHandler handler = new TimeRecordRowCallbackHandler();
        MapSqlParameterSource params = new MapSqlParameterSource("empIds", empId);
        remoteNamedJdbc.query(
                SqlTimeRecordQuery.GET_ACTIVE_TIME_REC_BY_EMP_IDS.getSql(schemaMap(), timeRecordOrder), params, handler);
        return handler.getRecordList();
    }

    /** {@inheritDoc} */
    @Override
    public List<Integer> getTimeRecordYears(Integer empId, SortOrder yearOrder) {
        SqlParameterSource params = new MapSqlParameterSource("empId", empId);
        OrderBy orderBy = new OrderBy("year", yearOrder);
        return remoteNamedJdbc.query(SqlTimeRecordQuery.GET_TREC_DISTINCT_YEARS.getSql(schemaMap(), orderBy), params,
                (rs, rowNum) -> rs.getInt("year"));
    }

    @Override
    public boolean saveRecord(TimeRecord record) {
        boolean isUpdate = true;
        if (record.getTimeRecordId() == null) {
            // Attempt to find existing record for employee with matching begin date
            // If that record exists, use that record id
            record.setTimeRecordId(getTimeRecordId(record));
        }

        MapSqlParameterSource params = getTimeRecordParams(record);
        if (record.getTimeRecordId() == null ||
                remoteNamedJdbc.update(SqlTimeRecordQuery.UPDATE_TIME_REC_SQL.getSql(schemaMap()), params)==0) {
            isUpdate = false;
            KeyHolder tsIdHolder = new GeneratedKeyHolder();
            if (remoteNamedJdbc.update(SqlTimeRecordQuery.INSERT_TIME_REC.getSql(schemaMap()), params,
                    tsIdHolder, new String[] {"NUXRTIMESHEET"}) == 0) {
                return false;
            }
            record.setTimeRecordId(((BigDecimal) tsIdHolder.getKeys().get("NUXRTIMESHEET")).toBigInteger());
            record.setUpdateDate(LocalDateTime.now());
        }
        // Insert each entry from the time record
        final Optional<TimeRecord> oldRecord = isUpdate ? Optional.of(getTimeRecord(record.getTimeRecordId())) : Optional.empty();

        for (TimeEntry entry : record.getTimeEntries()) {
            if (!shouldUpdate(entry, oldRecord)) {
                continue;
            }
            ensureId(entry, oldRecord);
            timeEntryDao.updateTimeEntry(entry);
            // Remove the entry from the time record if it is inactive
            if (!entry.isActive()) {
                record.removeEntry(entry.getDate());
            }
        }
        return true;
    }

    @Override
    public boolean deleteRecord(BigInteger recordId) {
        MapSqlParameterSource params = new MapSqlParameterSource("timesheetId", new BigDecimal(recordId));
        if (remoteNamedJdbc.update(SqlTimeRecordQuery.DELETE_TIME_REC_SQL.getSql(schemaMap()), params) > 0) {
            remoteNamedJdbc.update(SqlTimeRecordQuery.DELETE_TIME_REC_ENTRIES_SQL.getSql(schemaMap()), params);
            return true;
        }
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public boolean hasActiveEmployeeRecord(int supId) {
        MapSqlParameterSource params = new MapSqlParameterSource("supId", supId);
        Integer activeRecordCount = remoteNamedJdbc.queryForObject(SqlTimeRecordQuery.GET_SUP_TREC_COUNT.getSql(schemaMap()),
                params, Integer.class);
        return activeRecordCount > 0;
    }

    /** --- Helper Classes --- */

    private static class TimeRecordRowCallbackHandler implements RowCallbackHandler
    {
        private RemoteRecordRowMapper remoteRecordRowMapper = new RemoteRecordRowMapper();
        private RemoteEntryRowMapper remoteEntryRowMapper = new RemoteEntryRowMapper("ENT_");
        private PayPeriodRowMapper periodRowMapper = new PayPeriodRowMapper("PER_");
        private Map<BigDecimal, TimeRecord> recordMap = new HashMap<>();
        private List<TimeRecord> recordList = new ArrayList<>();

        @Override
        public void processRow(ResultSet rs) throws SQLException {
            BigDecimal recordId = rs.getBigDecimal("NUXRTIMESHEET");
            TimeRecord record;
            if (!recordMap.containsKey(recordId)) {
                record = remoteRecordRowMapper.mapRow(rs, 0);
                record.setPayPeriod(periodRowMapper.mapRow(rs, 0));
                recordMap.put(recordId, record);
                recordList.add(record);
            }
            else {
                record = recordMap.get(recordId);
            }
            rs.getDate("ENT_DTDAY");
            // If the day column was null, there was no entry because that column has a not null constraint
            if (!rs.wasNull()) {
                TimeEntry entry = remoteEntryRowMapper.mapRow(rs, 0);
                record.addTimeEntry(entry);
            }
        }

        public ListMultimap<Integer, TimeRecord> getRecordMap() {
            ListMultimap<Integer, TimeRecord> empRecordMap = ArrayListMultimap.create();
            recordList.forEach(record -> empRecordMap.put(record.getEmployeeId(), record));
            return empRecordMap;
        }

        public List<TimeRecord> getRecordList() {
            return recordList;
        }
    }

    public static MapSqlParameterSource getTimeRecordParams(TimeRecord timeRecord) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("timesheetId", timeRecord.getTimeRecordId() != null ?
                new BigDecimal(timeRecord.getTimeRecordId()) : null);
        params.addValue("empId", timeRecord.getEmployeeId());
        params.addValue("lastUser", timeRecord.getLastUser());
        params.addValue("tOriginalUserId", timeRecord.getOriginalUserId());
        params.addValue("tUpdateUserId", timeRecord.getUpdateUserId());
        params.addValue("tOriginalDate", SqlBaseDao.toDate(timeRecord.getCreatedDate()));
        params.addValue("tUpdateDate", SqlBaseDao.toDate(timeRecord.getUpdateDate()));
        params.addValue("status", timeRecord.isActive() ? "A" : "I");
        params.addValue("tSStatusId", timeRecord.getRecordStatus().getCode());
        params.addValue("beginDate", SqlBaseDao.toDate(timeRecord.getBeginDate()));
        params.addValue("endDate", SqlBaseDao.toDate(timeRecord.getEndDate()));
        params.addValue("remarks", timeRecord.getRemarks());
        params.addValue("supervisorId", timeRecord.getSupervisorId());
        params.addValue("excDetails", timeRecord.getExceptionDetails());
        params.addValue("procDate", SqlBaseDao.toDate(timeRecord.getProcessedDate()));
        params.addValue("respCtr", timeRecord.getRespHeadCode());
        params.addValue("approvalEmpId", timeRecord.getApprovalEmpId());

        return params;
    }

    /* --- Internal Methods --- */

    /**
     * Try to find a time record id for a time record
     * with the same employee id and begin date as the given time record
     * @param record {@link TimeRecord}
     * @return BigInteger
     */
    private BigInteger getTimeRecordId(TimeRecord record) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("empId", record.getEmployeeId())
                .addValue("beginDate", SqlBaseDao.toDate(record.getBeginDate()));
        // Attempt to find existing record for employee with matching begin date
        // If that record exists, use that record id
        try {
            BigDecimal id = remoteNamedJdbc.queryForObject(SqlTimeRecordQuery.GET_TREC_ID_BY_BEGIN_DATE.getSql(schemaMap()),
                    params, BigDecimal.class);
            return id.toBigInteger();
        } catch (EmptyResultDataAccessException ex) {
            return null;
        }
    }

    /**
     * @param entry TimeEntry - the time entry to insert
     * @param oldRecord TimeRecord - a record containing the last saved entry set
     * @return true if the entry is fundamentally different than the equivalent entry in oldRecord
     */
    private static boolean shouldUpdate(TimeEntry entry, Optional<TimeRecord> oldRecord) {
        Optional<TimeEntry> oldEntry = oldRecord.map(rec -> rec.getEntry(entry.getDate()));
        return oldEntry
                // Return true if the old record has the entry, and it is different
                .map(oldEnt -> !oldEnt.equals(entry))
                // Or the new entry is active and non-empty
                .orElse(!entry.isEmpty() && entry.isActive());
    }

    /**
     * Ensure that the time record id matches the id of the saved entry on the same date, if it exists
     */
    private static void ensureId(TimeEntry entry, Optional<TimeRecord> oldRecord) {
        oldRecord.map(rec -> rec.getEntry(entry.getDate()))
                .map(TimeEntry::getEntryId)
                .ifPresent(entry::setEntryId);
    }
}



