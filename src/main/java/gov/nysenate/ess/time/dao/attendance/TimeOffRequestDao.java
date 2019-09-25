package gov.nysenate.ess.time.dao.attendance;

import com.google.common.collect.Range;
import gov.nysenate.ess.core.dao.base.BaseDao;
import gov.nysenate.ess.time.model.attendance.*;

import java.time.LocalDate;
import java.util.List;

public interface TimeOffRequestDao extends BaseDao {

    /**
     * Retrieve a time off request given a specified request ID
     * @param requestId int
     * @return TimeOffRequest
     */
    TimeOffRequest getRequestById(int requestId) throws TimeOffRequestNotFoundException;

    /**
     * Retrieve all time off requests for an employee (Given
     * by employeeID) during a date range (Given by dateRange)
     * @param employeeId int
     * @return List<TimeOffRequest>
     */
    List<TimeOffRequest> getAllRequestsByEmpId(int employeeId, Range<LocalDate> dateRange);

    /**
     * Retrieve all time off requests for a supervisor (Given
     * by supervisor ID)
     * @param supervisorId int
     * @return List<TimeOffRequest>
     */
    List<TimeOffRequest> getAllRequestsBySupId(int supervisorId);

    /**
     * Retrieve all requests needing approval from a
     * specified supervisor
     * @param supervisorId int
     * @return List<TimeOffRequest>
     */
    List<TimeOffRequest> getRequestsNeedingApproval(int supervisorId);

    /**
     * Retrieve all active requests for a supervisor (Given
     * by supervisorId)
     * @param supervisorId int
     * @return List<TimeOffRequest>
     */
    List<TimeOffRequest> getActiveTimeOffRequests(int supervisorId);

    /**
     * Update the status of a request given the
     * request ID
     * @param request TimeOffRequest
     * @return int - The requestId if the request was added or updated,
     *               and -1 otherwise.
     */
    int updateRequest(TimeOffRequest request);

}
