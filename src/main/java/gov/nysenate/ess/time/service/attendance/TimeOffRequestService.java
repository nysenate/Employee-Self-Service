package gov.nysenate.ess.time.service.attendance;

import gov.nysenate.ess.time.model.attendance.TimeOffRequest;
import gov.nysenate.ess.time.model.attendance.TimeOffRequestNotFoundException;

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
     * Get all the active time off requests for a single employee
     *
     * @param empId int - employee id
     * @return List<TimeOffRequest> - empty list if none exist
     */
    List<TimeOffRequest> getActiveRequestsForEmp(int empId);

    /**
     * Get all time off requests needing approval for a single supervisor
     *
     * @param supId int - supervisor id
     * @return List<TimeOffRequest> - empty list if none exist
     */
    List<TimeOffRequest> getRequestsNeedingApproval(int supId);

    /**
     * Get all the time off requests for a given employee, supervisor, and year
     * (Provides a history of requests; the requests need not be active)
     *
     * @param empId int - employee id
     * @param supId int - supervisor id
     * @param year  int - year
     * @return List<TimeOffRequest> - empty list if none exist
     */
    List<TimeOffRequest> getRequests(int empId, int supId, int year);

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
     * @param request TimeOffRequest - the request that needs to be saved
     * @return boolean - true if request was saved/updated, false otherwise
     */
    boolean updateRequest(TimeOffRequest request);
}
