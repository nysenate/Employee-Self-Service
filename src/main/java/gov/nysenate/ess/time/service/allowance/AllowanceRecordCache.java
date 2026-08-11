package gov.nysenate.ess.time.service.allowance;

import gov.nysenate.ess.time.model.attendance.AttendanceRecord;
import gov.nysenate.ess.time.model.attendance.TimeRecord;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * A memo of the attendance and time records an allowance calculation reads, scoped to whatever the
 * caller decides.
 *
 * <p>Computing allowance usage for a date requires that employee's attendance and time records for
 * the whole surrounding year. Callers that ask for many dates in the same year — notably accrual
 * computation, which asks once per pay period — would otherwise re-read the same two result sets
 * from the remote SFMS database twenty odd times per request. Passing one instance of this class
 * through such a loop collapses those reads to one per employee-year.
 *
 * <p>This is deliberately <em>not</em> a shared or time-expiring cache. An instance holds records
 * only for as long as the caller holds it, so it cannot serve data from a previous request and
 * there is no staleness window: within a single computation the records were always going to be
 * identical, since nothing in that computation writes them. Callers that want the pre-existing
 * behaviour simply do not pass one, and a fresh instance is used for that single lookup.
 *
 * <p>Not thread safe. An instance is meant to stay on one thread for one computation.
 */
public final class AllowanceRecordCache {

    /** The records for a single employee and year. */
    static final class YearRecords {
        private final List<AttendanceRecord> attendanceRecords;
        private final List<TimeRecord> timeRecords;

        YearRecords(List<AttendanceRecord> attendanceRecords, List<TimeRecord> timeRecords) {
            this.attendanceRecords = attendanceRecords;
            this.timeRecords = timeRecords;
        }

        List<AttendanceRecord> getAttendanceRecords() {
            return attendanceRecords;
        }

        List<TimeRecord> getTimeRecords() {
            return timeRecords;
        }
    }

    private record Key(int empId, int year) {}

    private final Map<Key, YearRecords> cache = new HashMap<>();

    /** Returns the memoized records for this employee and year, loading them on first request. */
    YearRecords get(int empId, int year, Supplier<YearRecords> loader) {
        return cache.computeIfAbsent(new Key(empId, year), key -> loader.get());
    }
}
