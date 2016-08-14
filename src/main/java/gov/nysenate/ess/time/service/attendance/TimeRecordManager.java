package gov.nysenate.ess.time.service.attendance;

import gov.nysenate.ess.core.annotation.WorkInProgress;

/**
 * A service that is responsible for generating time records and ensuring that active time records contain valid data
 */
@WorkInProgress(author = "Sam", since = "2015/09/15", desc = "building and testing time record generation methods")
public interface TimeRecordManager {

    /**
     * Ensure that the given employee has records that cover the given pay periods
     * ensures that all records covering the pay periods contain correct and up to date employee information
     *
     * @param empId int - employee id
     * @return int - the number of records created/modified
     */
    public int ensureRecords(int empId);

    /**
     * Ensure that all active employees have up to date, correct records for all active pay periods in the current year
     * @see #ensureRecords(int)
     */
    public void ensureAllActiveRecords();
}
