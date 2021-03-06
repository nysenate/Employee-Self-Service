package gov.nysenate.ess.time.dao.attendance;

import com.google.common.collect.ListMultimap;
import com.google.common.collect.Range;
import com.google.common.collect.RangeSet;
import gov.nysenate.ess.core.model.period.PayPeriod;
import gov.nysenate.ess.time.model.attendance.AttendanceRecord;

import java.time.LocalDate;
import java.util.List;
import java.util.SortedSet;

public interface AttendanceDao
{
    /**
     * Get a set of attendance years for which an employee can enter time
     * @param empId
     * @return
     */
    SortedSet<Integer> getOpenAttendanceYears(Integer empId);

    /**
     * Get a range set of dates encompassing non-closed attendance years for the given employee.
     * @param empId Integer
     * @return RangeSet<LocalDate>
     */
    RangeSet<LocalDate> getOpenDates(Integer empId);

    /**
     * Get a set of years for which the employee has attendance records
     * @param empId Integer - employee id
     * @return SortedSet<Integer>
     */
    SortedSet<Integer> getAttendanceYears(Integer empId);

    /**
     * Get attendance records for all employees for any open attendance years
     * @return ListMultimap<Integer, AttendanceRecord>
     */
    ListMultimap<Integer, AttendanceRecord> getOpenAttendanceRecords();

    /**
     * Get attendance records for the specified employees for their open attendance years
     * @param empId Integer - employee id
     * @return List<AttendanceRecord>
     */
    List<AttendanceRecord> getOpenAttendanceRecords(Integer empId);

    /**
     * Attempt to get an attendance record for the given employee / pay period.
     * @param empId int
     * @param period {@link PayPeriod}
     * @return {@link AttendanceRecord}
     * @throws AttendanceRecordNotFoundEx if the requested record is not found
     */
    AttendanceRecord getAttendanceRecord(Integer empId, PayPeriod period) throws AttendanceRecordNotFoundEx;

    /**
     * Get attendance records for the specified employee for the given year
     * Records are ordered by date in ascending order
     *
     * @param empId Integer
     * @param year Integer
     * @return List<AttendanceRecord>
     */
    List<AttendanceRecord> getAttendanceRecords(Integer empId, Integer year);

    /**
     * Get attendance records for the specified employee intersecting with the given dates
     * Records are ordered by date in ascending order
     *
     * @param empId Integer
     * @param dates Range<LocalDate>
     * @return List<AttendanceRecord>
     */
    List<AttendanceRecord> getAttendanceRecords(Integer empId, Range<LocalDate> dates);
}
