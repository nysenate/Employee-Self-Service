package gov.nysenate.ess.seta.controller.api;

import gov.nysenate.ess.core.client.response.base.BaseResponse;
import gov.nysenate.ess.core.client.response.base.ListViewResponse;
import gov.nysenate.ess.core.controller.api.BaseRestApiCtrl;
import gov.nysenate.ess.seta.client.view.AttendanceRecordView;
import gov.nysenate.ess.seta.dao.attendance.AttendanceDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;

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
     * Request Parameters: empId - int[] - required - Records will be retrieved for these employee ids
     *                     year - int - Specifies the attendance year for which records will be retrieved
     */
    @RequestMapping(value = "records", method = RequestMethod.GET, params = {"empId", "year"})
    public BaseResponse getAttendanceRecords(@RequestParam Integer empId, @RequestParam Integer year) {
        return ListViewResponse.of(
                attendanceDao.getAttendanceRecords(empId, year).stream()
                        .map(AttendanceRecordView::new)
                        .collect(Collectors.toList()),
                "records"
        );
    }

}
