package gov.nysenate.ess.seta.controller.api;

import com.google.common.collect.Range;
import gov.nysenate.ess.core.client.response.base.BaseResponse;
import gov.nysenate.ess.core.client.response.base.ListViewResponse;
import gov.nysenate.ess.core.controller.api.BaseRestApiCtrl;
import gov.nysenate.ess.core.util.DateUtils;
import gov.nysenate.ess.seta.client.view.AttendanceRecordView;
import gov.nysenate.ess.seta.dao.attendance.AttendanceDao;
import gov.nysenate.ess.seta.model.attendance.AttendanceRecord;
import gov.nysenate.ess.seta.model.auth.EssTimePermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static gov.nysenate.ess.seta.model.auth.TimePermissionObject.*;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@RequestMapping(BaseRestApiCtrl.REST_PATH + "/attendance")
public class AttendanceRestApiCtrl extends BaseRestApiCtrl {

    @Autowired AttendanceDao attendanceDao;

    /**
     * Get Attendance Record API
     * -------------------
     *
     * Get all attendance records for one employee during the given year:
     * (GET) /api/v1/attendance/records[.json]
     *
     * Request Parameters:
     * @param empId - Integer - required - Records will be retrieved for these employee ids
     * @param year - Integer - Specifies the attendance year for which records will be retrieved
     */
    @RequestMapping(value = "records", method = GET, params = {"empId", "year"})
    public BaseResponse getAttendanceRecords(@RequestParam Integer empId, @RequestParam Integer year) {
        checkPermission(new EssTimePermission( empId, ATTENDANCE_RECORDS, GET, DateUtils.yearDateRange(year)));

        return getRecordResponse(attendanceDao.getAttendanceRecords(empId, year));
    }

    /**
     * Get Attendance Record API
     * -------------------
     *
     * Get all attendance records for one employee during the given date range:
     * (GET) /api/v1/attendance/records[.json]
     *
     * Request Parameters:
     * @param empId - Integer - required - Records will be retrieved for these employee ids
     * @param from - String (ISO 8601 Date) - Records will be retrieved starting from this date
     * @param to - String (ISO 8601 Date) - Records will be retrieved up to this date
     *
     */
    @RequestMapping(value = "records", method = GET, params = {"empId", "from", "to"})
    public BaseResponse getAttendanceRecords(@RequestParam Integer empId,
                                             @RequestParam String from,
                                             @RequestParam String to) {
        LocalDate fromDate = parseISODate(from, "from");
        LocalDate toDate = parseISODate(to, "to");
        Range<LocalDate> dateRange = getClosedRange(fromDate, toDate, "from", "to");

        checkPermission(new EssTimePermission( empId, ATTENDANCE_RECORDS, GET, dateRange));

        return getRecordResponse(attendanceDao.getAttendanceRecords(empId, dateRange));
    }

    /** --- Internal Methods --- */

    private ListViewResponse<AttendanceRecordView> getRecordResponse(List<AttendanceRecord> records) {
        return ListViewResponse.of(
                records.stream()
                        .map(AttendanceRecordView::new)
                        .collect(Collectors.toList()),
                "records"
        );
    }

}
