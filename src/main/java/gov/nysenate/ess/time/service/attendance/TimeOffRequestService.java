package gov.nysenate.ess.time.service.attendance;

import com.google.common.collect.Range;
import gov.nysenate.ess.time.model.attendance.TimeOffRequest;
import gov.nysenate.ess.time.model.attendance.TimeOffRequestNotFoundException;

import java.sql.Time;
import java.time.LocalDate;
import java.util.List;

public interface TimeOffRequestService {

    /**
     * Get the Time Off Request with the given id
     *
     * @param requestId int
     * @return TimeOffRequest
     * @throws TimeOffRequestNotFoundException if the request does not exist
     */
    TimeOffRequest getTimeOffRequest(int requestId) throws TimeOffRequestNotFoundException;

    /**
     * Get all requests for an employee for a given date range.
     *
     * @param empId int - employee id
     * @return dateRange Range<LocalDate>
     */
    List<TimeOffRequest> getAllRequestForEmpDateRange(int empId, Range<LocalDate> dateRange);

    /**
     * Get all time off requests needing approval for a single supervisor
     *
     * @param supId int - supervisor id
     * @return List<TimeOffRequest> - empty list if none exist
     */
    List<TimeOffRequest> getRequestsNeedingApproval(int supId);

    /**
     * Get all active requests for a supervisor's employees
     * (Provides the supervisor with information on future attendance)
     *
     * @param supId - supervisor id
     * @returns List<TimeOffRequest> - empty list if none exist
     */
    List<TimeOffRequest> getActiveRequestsForSup(int supId);

    /**
     * Update the data from user input for a given request
     *
     * @param request TimeOffRequest - the request that needs to be saved or added
     * @return int - The requestId if the request was saved/updated or added,
     *               and -1 otherwise.
     */
    int updateRequest(TimeOffRequest request);
}
