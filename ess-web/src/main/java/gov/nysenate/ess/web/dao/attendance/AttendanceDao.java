package gov.nysenate.ess.web.dao.attendance;

import com.google.common.collect.ListMultimap;
import com.google.common.collect.RangeSet;
import gov.nysenate.ess.web.model.attendance.AttendanceRecord;

import java.time.LocalDate;
import java.util.List;
import java.util.SortedSet;

public interface AttendanceDao {

    /**
     * Get a set of attendance years for which an employee can enter time
     * @param empId
     * @return
     */
    public SortedSet<Integer> getOpenAttendanceYears(Integer empId);

    public RangeSet<LocalDate> getOpenDates(Integer empId);

    /**
     * Get attendance records for all employees for any open attendance years
     * @return ListMultimap<Integer, AttendanceRecord>
     */
    public ListMultimap<Integer, AttendanceRecord> getOpenAttendanceRecords();

    /**
     * Get attendance records for the specified employees for their open attendance years
     * @param empId Integer - employee id
     * @return List<AttendanceRecord>
     */
    public List<AttendanceRecord> getOpenAttendanceRecords(Integer empId);
}
