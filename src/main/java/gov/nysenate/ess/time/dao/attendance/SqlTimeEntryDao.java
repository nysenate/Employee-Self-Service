package gov.nysenate.ess.time.dao.attendance;

import gov.nysenate.ess.core.util.OrderBy;
import gov.nysenate.ess.core.util.SortOrder;
import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import gov.nysenate.ess.time.dao.attendance.mapper.RemoteEntryRowMapper;
import gov.nysenate.ess.time.model.attendance.TimeEntry;
import gov.nysenate.ess.time.model.attendance.TimeEntryException;
import gov.nysenate.ess.time.model.attendance.TimeEntryNotFoundEx;
import org.apache.commons.lang3.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

@Repository
public class SqlTimeEntryDao extends SqlBaseDao implements TimeEntryDao
{
    private static final Logger logger = LoggerFactory.getLogger(SqlTimeEntryDao.class);

    @Override
    public TimeEntry getTimeEntryById(BigInteger timeEntryId) throws TimeEntryException {
        throw new NotImplementedException("getTimeEntryById not yet implemented");
    }

    /**
     * {@inheritDoc}
     *
     * @param timeRecordId
     */
    @Override
    public List<TimeEntry> getTimeEntriesByRecordId(BigInteger timeRecordId) throws TimeEntryException {
        List<TimeEntry> timeEntryList;
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("status", "A");
        params.addValue("timesheetId", new BigDecimal(timeRecordId));
        try {
            timeEntryList = remoteNamedJdbc.query(
                    SqlTimeEntryQuery.SELECT_TIME_ENTRIES_BY_TIME_RECORD_ID.getSql(schemaMap(),
                            new OrderBy("DTDAY", SortOrder.ASC)), params, new RemoteEntryRowMapper());
        }
        catch (DataRetrievalFailureException ex) {
            logger.warn("Retrieve time entries for record {} error: {}", timeRecordId, ex.getMessage());
            throw new TimeEntryNotFoundEx("No matching TimeEntries for TimeRecord id: " + timeRecordId);
        }
        return timeEntryList;
    }

    @Override
    public void updateTimeEntry(TimeEntry timeEntry) {
        MapSqlParameterSource params = getTimeEntryParams(timeEntry);
        if (remoteNamedJdbc.update(SqlTimeEntryQuery.UPDATE_TIME_ENTRY.getSql(schemaMap()), params) == 0) {
            KeyHolder entryIdHolder = new GeneratedKeyHolder();
            remoteNamedJdbc.update(SqlTimeEntryQuery.INSERT_TIME_ENTRY.getSql(schemaMap()), params, entryIdHolder, new String[]{"NUXRDAY"});
            timeEntry.setEntryId(((BigDecimal) entryIdHolder.getKeys().get("NUXRDAY")).toBigInteger());
        }
    }

    private static MapSqlParameterSource getTimeEntryParams(TimeEntry timeEntry) {
        MapSqlParameterSource param = new MapSqlParameterSource();
        param.addValue("tSDayId", timeEntry.getEntryId() != null ? new BigDecimal(timeEntry.getEntryId()) : null);
        param.addValue("timesheetId", new BigDecimal(timeEntry.getTimeRecordId()));
        param.addValue("empId", timeEntry.getEmpId());
        param.addValue("lastUpdater", timeEntry.getEmployeeName());
        param.addValue("dayDate", SqlBaseDao.toDate(timeEntry.getDate()));
        param.addValue("workHR", timeEntry.getWorkHours().orElse(null));
        param.addValue("travelHR", timeEntry.getTravelHours().orElse(null));
        param.addValue("holidayHR", timeEntry.getHolidayHours().orElse(null));
        param.addValue("vacationHR", timeEntry.getVacationHours().orElse(null));
        param.addValue("personalHR", timeEntry.getPersonalHours().orElse(null));
        param.addValue("sickEmpHR", timeEntry.getSickEmpHours().orElse(null));
        param.addValue("sickFamilyHR", timeEntry.getSickFamHours().orElse(null));
        param.addValue("miscHR", timeEntry.getMiscHours().orElse(null));
        param.addValue("miscTypeId", timeEntry.getMiscType() != null ?
                new BigDecimal(timeEntry.getMiscType().getMiscLeaveId()) : new BigDecimal(BigInteger.ZERO));
        param.addValue("tOriginalUserId", timeEntry.getOriginalUserId());
        param.addValue("tUpdateUserId", timeEntry.getUpdateUserId());
        param.addValue("tOriginalDate", SqlBaseDao.toDate(timeEntry.getOriginalDate()));
        param.addValue("tUpdateDate", SqlBaseDao.toDate(timeEntry.getUpdateDate()));
        param.addValue("status", String.valueOf(getStatusCode(timeEntry.isActive())));
        param.addValue("accruing", String.valueOf(getAccruingCode(timeEntry.isAccruing())));
        param.addValue("empComment", timeEntry.getEmpComment());
        param.addValue("payType", timeEntry.getPayType() != null ? timeEntry.getPayType().name() : null);
        return param;
    }
}