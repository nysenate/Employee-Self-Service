package gov.nysenate.ess.time.dao.attendance;

import gov.nysenate.ess.core.dao.base.BaseDao;
import gov.nysenate.ess.time.model.attendance.TimeEntry;
import gov.nysenate.ess.time.model.attendance.TimeEntryException;

import java.math.BigInteger;
import java.util.List;

public interface TimeEntryDao extends BaseDao
{
    /**
     * Retrieve a time entry from its unique id
     * @param timeEntryId
     * @return TimeEntry
     */
    TimeEntry getTimeEntryById(BigInteger timeEntryId);

    /**
     * Retrieve all time entries that belong to a specific time record.
     * @param timeRecordId int - Id of the parent TimeRecord
     * @return List<TimeEntry>
     * @throws TimeEntryException - TimeEntryNotFoundEx if no matching time entries were found
     */
    List<TimeEntry> getTimeEntriesByRecordId(BigInteger timeRecordId) throws TimeEntryException;

    /**
     * Update or insert a time entry using TimeEntry Object
     * @param tsd - TimeEntry class object containing data to be updated
     */
    void updateTimeEntry(TimeEntry tsd);
  }
