package gov.nysenate.ess.time.controller.api;

import com.google.common.collect.ImmutableSet;
import gov.nysenate.ess.core.controller.api.BaseRestApiCtrl;
import gov.nysenate.ess.core.model.period.PayPeriod;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.service.period.PayPeriodService;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import gov.nysenate.ess.time.model.auth.EssTimePermission;
import gov.nysenate.ess.time.model.auth.TimePermissionObject;
import gov.nysenate.ess.time.service.ReportUrlService;
import gov.nysenate.ess.time.service.attendance.TimeRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.math.BigInteger;
import java.net.URL;
import java.time.LocalDate;
import java.util.Optional;

import static gov.nysenate.ess.core.model.period.PayPeriodType.AF;
import static gov.nysenate.ess.time.model.auth.TimePermissionObject.*;
import static org.springframework.http.MediaType.APPLICATION_PDF_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@RequestMapping(BaseRestApiCtrl.REST_PATH)
public class ReportRestApiCtrl extends BaseRestApiCtrl {

    private static final ImmutableSet<TimePermissionObject> attendanceReportPermissions =
            ImmutableSet.of(ACCRUAL, ALLOWANCE, ATTENDANCE_RECORDS, TIME_RECORDS);

    private final ReportUrlService reportUrlService;
    private final PayPeriodService payPeriodService;
    private final EmployeeInfoService empInfoService;
    private final TimeRecordService timeRecordService;

    @Autowired
    public ReportRestApiCtrl(ReportUrlService reportUrlService, PayPeriodService payPeriodService,
                             EmployeeInfoService empInfoService, TimeRecordService timeRecordService) {
        this.reportUrlService = reportUrlService;
        this.payPeriodService = payPeriodService;
        this.empInfoService = empInfoService;
        this.timeRecordService = timeRecordService;
    }

    /**
     * Get Accrual Report API
     * -------------------------
     *
     * Generates a PDF accrual report for the given employee on the pay period of the given date.
     * (GET) /api/v1/accrual/report
     *
     * Request Parameters: empId - int - required - The employee for the report
     *                     date - Date - required - Determines pay period of the report
     */
    @GetMapping(value = "/accrual/report", produces = APPLICATION_PDF_VALUE)
    public ResponseEntity<InputStreamResource> getAccrualReport(
            @RequestParam int empId,
            @RequestParam String date) throws IOException {
        LocalDate parsedDate = parseISODate(date, "date");
        PayPeriod payPeriod = payPeriodService.getPayPeriod(AF, parsedDate);
        return getPdfReport(empId, payPeriod, reportUrlService.getAccrualReportUrl(empId, payPeriod));
    }

    /**
     * Get Attendance Report API
     * -------------------------
     *
     * Generates a pdf attendance report for the given employee on the pay period of the given date.
     * (GET) /api/v1/attendance/report
     *
     * Request Parameters: timeRecordId - BigInteger - required - The time record ID for the report
     */
    @GetMapping(value = "/attendance/report", produces = APPLICATION_PDF_VALUE)
    public ResponseEntity<InputStreamResource> getAttendancePdfReport(@RequestParam String timeRecordId) throws IOException {
        var timeRecord = timeRecordService.getTimeRecord(new BigInteger(timeRecordId));
        PayPeriod payPeriod = payPeriodService.getPayPeriod(AF, timeRecord.getEndDate());
        return getPdfReport(timeRecord.getEmployeeId(), payPeriod,
                reportUrlService.getAttendanceReportUrl(timeRecordId));
    }

    /**
     * Helper method to produce a PDF.
     * @return the properly named PDF.
     */
    private ResponseEntity<InputStreamResource> getPdfReport(int empId, PayPeriod payPeriod, URL url) throws IOException {
        // Require permissions that were valid at some point during the requested pay period.
        attendanceReportPermissions.stream()
                .map(po -> new EssTimePermission(empId, po, GET, payPeriod.getDateRange(), false))
                .forEach(this::checkPermission);
        Employee employee = empInfoService.getEmployee(empId);

        String uid = Optional.ofNullable(employee.getUid()).orElse(Integer.toString(empId));
        String filename = uid + "_" + payPeriod.getEndDate() + ".pdf";
        return ResponseEntity.ok()
                .header("Content-Disposition", "inline; filename=\"" + filename + "\"")
                .header("Content-Type", APPLICATION_PDF_VALUE + "; name=\"" + filename + "\"")
                .body(new InputStreamResource(url.openStream()));
    }
}
