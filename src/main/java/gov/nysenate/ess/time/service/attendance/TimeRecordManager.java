package gov.nysenate.ess.time.service.attendance;

import gov.nysenate.ess.core.annotation.WorkInProgress;
import gov.nysenate.ess.core.model.period.PayPeriod;

import java.util.Collection;

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
     * @param payPeriods
     * @return int - the number of records created/modified
     */
    int ensureRecords(int empId, Collection<PayPeriod> payPeriods);

    /**
     * Overload of {@link #ensureRecords(int, Collection)} that uses all open pay periods for the given year
     * @see #ensureRecords(int, Collection)
     * @param empId int - employee id
     * @return int - the number of records created/modified
     */
    int ensureRecords(int empId);


    /**
     * Ensure that all active employees have up to date, correct records for all active pay periods in the current year
     * @see #ensureRecords(int)
     */
    void ensureAllActiveRecords();
}
