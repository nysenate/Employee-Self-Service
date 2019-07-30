package gov.nysenate.ess.time.dao.attendance;

import gov.nysenate.ess.core.dao.base.BaseDao;
import gov.nysenate.ess.time.model.attendance.*;

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
     * by employee ID)
     * @param employeeId int
     * @return List<TimeOffRequest>
     */
    List<TimeOffRequest> getAllRequestsByEmpId(int employeeId);

    /**
     * Retrieve all time off requests for a supervisor (Given
     * by supervisor ID)
     * @param supervisorId int
     * @return List<TimeOffRequest>
     */
    List<TimeOffRequest> getAllRequestsBySupId(int supervisorId);

    /**
     * Retrieve all requests from a specified employee to a
     * specified supervisor during a given year
     * @param employeeId int
     * @param supervisorId int
     * @param year int
     * @return List<TimeOffRequest>
     */
    List<TimeOffRequest> getAllRequestsBySupEmpYear(int employeeId, int supervisorId, int year);

    /**
     * Add a comment to the comment thread of a request
     * @param comment TimeOffRequestComment
     */
    void addCommentToRequest(TimeOffRequestComment comment);

    /**
     * Add a day with time off to a request
     * @param day TimeOffRequestDay
     */
    void addDayToRequest(TimeOffRequestDay day);

    /**
     * Create a new time off request
     * @param request TimeOffRequest
     * @return int (the requestId of the newly added request)
     */
    int addNewRequest(TimeOffRequest request);

    /**
     * Update the status of a request given the
     * request ID
     * @param request TimeOffRequest
     */
    void updateRequest(TimeOffRequest request);

    /**
     * Delete all comments for a request
     * @param requestId int
     */
    void removeAllComments(int requestId);

    /**
     * Delete all days for a request
     * @param requestId int
     */
    void removeAllDays(int requestId);
}
