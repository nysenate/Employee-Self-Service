package gov.nysenate.ess.seta.service.attendance;

import com.google.common.collect.ListMultimap;
import com.google.common.collect.Range;
import gov.nysenate.ess.core.util.SortOrder;
import gov.nysenate.ess.core.annotation.WorkInProgress;
import gov.nysenate.ess.seta.model.attendance.TimeRecord;
import gov.nysenate.ess.seta.model.attendance.TimeRecordStatus;
import gov.nysenate.ess.seta.model.personnel.SupervisorException;
import gov.nysenate.ess.core.model.period.PayPeriod;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@WorkInProgress(author = "Ash", since = "2015/09/11", desc = "Reworking methods in the class")
public interface TimeRecordService
{
    /**
     * Gets the distinct years that an employee has at least one time record for.
     * @param empId Integer - employee id
     * @param yearOrder - SortOrder - order the returned years
     * @return List<Integer>
     */
    List<Integer> getTimeRecordYears(Integer empId, SortOrder yearOrder);

    /**
     * Gets a list of active time records. 'Active' records in this case are simply records that
     * have an employee or supervisor scope. Records that have already been sent to personnel or
     * approved by personnel are not active.
     *
     * @param empId Integer
     * @return List<TimeRecord>
     */
    List<TimeRecord> getActiveTimeRecords(Integer empId);

    /**
     * Helper method to return just the active time records that are scoped to the 'employee'.
     *
     * @param empId Integer
     * @return List<TimeRecord>
     */
    default List<TimeRecord> getEmployeeScopedTimeRecords(Integer empId) {
        return getActiveTimeRecords(empId).stream()
            .filter(tr -> tr.getRecordStatus().isUnlockedForEmployee())
            .collect(Collectors.toList());
    }

    /**
     * Helper method to return just the active time records that are scoped to the 'supervisor'.
     *
     * @param empId Integer
     * @return List<TimeRecord>
     */
    default List<TimeRecord> getSupervisorScopedTimeRecords(Integer empId) {
        return getActiveTimeRecords(empId).stream()
            .filter(tr -> tr.getRecordStatus().isUnlockedForSupervisor())
            .collect(Collectors.toList());
    }

    /**
     * Get time records for one or more employees, matching certain time record statuses, over a specified date range.
     *
     * @param empIds Set<Integer> - employee ids
     * @param dateRange Range<LocalDate> - interval to check for
     * @param statuses Set<TimeRecordStatus> - time record statuses to retrieve
     * @return List<TimeRecord>
     */
    List<TimeRecord> getTimeRecords(Set<Integer> empIds, Range<LocalDate> dateRange, Set<TimeRecordStatus> statuses);

    /**
     * Get time records for one or more employees, matching certain time record statuses, for the specified pay periods
     *
     * @param empIds Set<Integer> - employee ids
     * @param payPeriods Collection<PayPeriod> - pay periods
     * @param statuses Set<TimeRecordStatus> - time record statuses to retrieve
     * @return List<TimeRecord>
     */
    List<TimeRecord> getTimeRecords(Set<Integer> empIds, Collection<PayPeriod> payPeriods, Set<TimeRecordStatus> statuses);

    /**
     * Retrieves the time records for an employee with employee id 'empId' that were to be approved by the given
     * supervisor with id 'supId' that are contained during the 'dateRange'.
     *
     * @param empId Integer - Employee id to retrieve records for
     * @param supId Integer - Supervisor id that managed the set of records we are returning
     * @param dateRange Range<LocalDate> - Returned records will be contained within this date range
     * @return List<TimeRecord>
     */
    /** FIXME: Is this needed? Maybe for the emp group history viewer? */
    List<TimeRecord> getTimeRecordsWithSupervisor(Integer empId, Integer supId, Range<LocalDate> dateRange);

    /**
     * Retrieve time records for which the given supervisor id is the supervisor or supervisor override
     * @param supId int - employee id
     * @param dateRange Range<LocalDate> - date range to query over
     * @param statuses Set<TimeRecordStatus> - time record statuses to retrieve
     * @return ListMultimap<Integer, TimeRecord> - Mapping of original supervisor id -> time records under that supervisor
     */
    ListMultimap<Integer, TimeRecord> getSupervisorRecords(int supId, Range<LocalDate> dateRange,
                                                           Set<TimeRecordStatus> statuses) throws SupervisorException;

    /**
     * @see #getSupervisorRecords(int, Range, Set)
     * An overload that gets supervisor employee records that are still in progress i.e. not approved by personnel
     */
    default ListMultimap<Integer, TimeRecord> getSupervisorRecords(int supId, Range<LocalDate> dateRange)
            throws SupervisorException {
        return getSupervisorRecords(supId, dateRange, TimeRecordStatus.inProgress());
    }

    /**
     *
     * @param record - TimeRecord class object containing data to be updated into the table
     * @return Boolean value, true if data successfully updated else false.
     */
    boolean saveRecord(TimeRecord record);

    /**
     * Remove the time record with the specified time record id
     * @param timeRecordId BigInteger
     * @return true if a time record was removed
     */
    boolean deleteRecord(BigInteger timeRecordId);

    /**
     * @see #deleteRecord(BigInteger)
     */
    default boolean deleteRecord(TimeRecord timeRecord) {
        return timeRecord.getTimeRecordId() != null && deleteRecord(timeRecord.getTimeRecordId());
    }
}