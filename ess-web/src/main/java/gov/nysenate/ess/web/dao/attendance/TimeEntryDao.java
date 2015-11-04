package gov.nysenate.ess.web.dao.attendance;

import gov.nysenate.ess.web.model.attendance.TimeEntry;
import gov.nysenate.ess.core.dao.base.BaseDao;
import gov.nysenate.ess.web.model.attendance.TimeEntryException;

import java.math.BigInteger;
import java.util.List;

public interface TimeEntryDao extends BaseDao
{
    /**
     * Retrieve a time entry from its unique id
     * @param timeEntryId
     * @return TimeEntry
     * @throws TimeEntryException - TimeEntryNotFoundEx if no matching time entries were found
     */
    public TimeEntry getTimeEntryById(BigInteger timeEntryId) throws TimeEntryException;

    /**
     * Retrieve all time entries that belong to a specific time record.
     * @param timeRecordId int - Id of the parent TimeRecord
     * @return List<TimeEntry>
     * @throws TimeEntryException - TimeEntryNotFoundEx if no matching time entries were found
     */
    public List<TimeEntry> getTimeEntriesByRecordId(BigInteger timeRecordId) throws TimeEntryException;

    /**
     * Update or insert a time entry using TimeEntry Object
     * @param tsd - TimeEntry class object containing data to be updated
     * @return boolean, true if data successfully updated, otherwise false.
     */
    public void updateTimeEntry(TimeEntry tsd);
  }
