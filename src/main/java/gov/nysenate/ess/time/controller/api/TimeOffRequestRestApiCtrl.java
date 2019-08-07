package gov.nysenate.ess.time.controller.api;

import com.google.common.collect.Range;
import gov.nysenate.ess.core.client.response.base.BaseResponse;
import gov.nysenate.ess.core.client.response.error.ErrorCode;
import gov.nysenate.ess.core.client.response.error.ViewObjectErrorResponse;
import gov.nysenate.ess.core.controller.api.BaseRestApiCtrl;
import gov.nysenate.ess.core.model.base.InvalidRequestParamEx;
import gov.nysenate.ess.time.client.view.attendance.TimeOffRequestView;
import gov.nysenate.ess.time.model.attendance.*;
import gov.nysenate.ess.time.model.auth.EssTimePermission;
import gov.nysenate.ess.time.service.attendance.EssTimeOffRequestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static gov.nysenate.ess.time.model.auth.TimePermissionObject.*;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping(BaseRestApiCtrl.REST_PATH + "/accruals/request")
public class TimeOffRequestRestApiCtrl extends BaseRestApiCtrl {

    private static final Logger logger = LoggerFactory.getLogger(TimeOffRequestRestApiCtrl.class);

    @Autowired
    EssTimeOffRequestService timeOffRequestService;

    /**
     * Get Request API
     * ---------------
     *
     * Get a single Time Off Request for a
     * given requestId:
     * (GET) /api/v1/timeoffrequests/getrequest
     *
     * @param requestId int
     * @return TimeOffRequest
     */
    @RequestMapping(value="/{requestId:\\d+}", method = RequestMethod.GET)
    public TimeOffRequestView getRequest(@PathVariable int requestId) {
        TimeOffRequest request =  timeOffRequestService.getTimeOffRequest(requestId);
        checkPermission(new EssTimePermission(request.getEmployeeId(), TIME_OFF_REQUESTS, GET, LocalDateTime.now()));
        return new TimeOffRequestView(request);
    }

    /**
     * Get an Employee's Time Off Requests API
     * ---------------------------------------
     *
     * Get all active time off requests for a given
     * employee:
     * (GET) /api/v1/timeoffrequests/getemployeerequests
     *
     * @param empId int
     * @return List<TimeOffRequests>
     */
    @RequestMapping(value="/employee/{empId:\\d+}", method = RequestMethod.GET)
    public List<TimeOffRequestView> getEmployeeRequestsJson(@PathVariable int empId) {
        checkPermission(new EssTimePermission(empId, TIME_OFF_REQUESTS, GET, LocalDate.now()));
        List<TimeOffRequest> requests = timeOffRequestService.getActiveRequestsForEmp(empId);
        return getListRequestViews(requests);
    }

    /**
     * Get a Supervisor's Time Off Requests API
     * ----------------------------------------
     *
     * Get all active requests for all employees for
     * a given supervisor:
     * (GET) /api/v1/timeoffrequests/getsupervisorrequests
     *
     * @param supId int
     * @return List<TimeOffRequests>
     */
    @RequestMapping(value="/supervisor/{supId:\\d+}/active", method = RequestMethod.GET)
    public List<TimeOffRequestView> getSupervisorRequestsJson(@PathVariable int supId) {
        checkPermission(new EssTimePermission(supId, SUPERVISOR_TIME_RECORDS, GET, LocalDate.now()));
        List<TimeOffRequest> requests = timeOffRequestService.getActiveRequestsForSup(supId);
        return getListRequestViews(requests);
    }

    /**
     * Get Requests By Year API
     * ------------------------
     *
     * Get all requests for a specified employee,
     * supervisor, and year:
     * (GET) /api/v1/timeoffrequests/getrequestsbyyear
     *
     * @param supId int
     * @param empId int
     * @param year int
     * @return List<TimeOffRequest>
     */
    @RequestMapping(value = "/supervisor/{supId:\\d+}", method = RequestMethod.GET)
    public List<TimeOffRequestView> getRequestsByYear(@PathVariable int supId, @RequestParam int empId, @RequestParam int year) {
        checkPermission(new EssTimePermission(supId, SUPERVISOR_TIME_RECORDS, GET, LocalDate.now()));
        List<TimeOffRequest> requests = timeOffRequestService.getRequests(empId, supId, year);
        return getListRequestViews(requests);
    }

    /**
     * Update Request API
     * ------------------
     *
     * Update a request, identified by a given
     * request id:
     * (POST) /api/v1/timeoffrequests/updaterequest
     *
     * Post Data: json TimeOffRequestView
     * @returns boolean - True if updated/saved properly, false otherwise
     */
    @RequestMapping(value = "", method = POST)
    public boolean updateRequest(@RequestBody TimeOffRequestView request) {
        checkPermission(new EssTimePermission(request.getEmployeeId(), TIME_OFF_REQUESTS, POST, LocalDate.now()));
        TimeOffRequest timeOffRequest = request.toTimeOffRequest();
        return timeOffRequestService.updateRequest(timeOffRequest);
    }

    /**
     * Review Time Off Request API
     * ---------------------------
     *
     * Review a Time Off Request:
     *      (POST) /api/v1/accruals/request/review
     *
     * Request Params:
     * @param requestId int - id of the reviewed time off request
     * @param comment String - any comments attached to the review (from the supervisor)
     * @param action String - {@link TimeOffRequestAction} - action to take on the request
     */
    @RequestMapping(value = "/review/{requestId:\\d+}", method = POST)
    public void reviewRequest(@PathVariable int requestId,
                              @RequestParam(required = false) String comment,
                              @RequestParam String action) {
        TimeOffRequest request = timeOffRequestService.getTimeOffRequest(requestId);

        checkPermission(new EssTimePermission(request.getSupervisorId(), TIME_OFF_REQUEST_REVIEW, POST,
                Range.closedOpen(request.getStartDate(), request.getEndDate().plusDays(1))));

        TimeOffRequestAction timeOffRequestAction = getEnumParameter("action", action,
                TimeOffRequestAction.class);
        if(timeOffRequestAction == TimeOffRequestAction.SAVE) {
            throw new InvalidRequestParamEx(action, "action", "String",
                    "action != SAVE");
        }
        if(timeOffRequestAction == TimeOffRequestAction.SUBMIT) {
            throw new InvalidRequestParamEx(action, "action", "String",
                    "action != SUBMIT");
        }

        if(comment != null) {
            TimeOffRequestComment newComment = new TimeOffRequestComment(requestId, request.getSupervisorId(), comment);
            List<TimeOffRequestComment> originalComments = request.getComments();
            originalComments.add(newComment);
            request.setComments(originalComments);
        }
        if(timeOffRequestAction == TimeOffRequestAction.APPROVE) {
            request.setStatus(TimeOffStatus.APPROVED);
        } else { //supervisor did not approve the request
            request.setStatus(TimeOffStatus.DISAPPROVED);
        }

        //update the request to save the changes that were just made
        timeOffRequestService.updateRequest(request);
    }

    /**
     * Handle case where a time off request does not exist for the given requestId
     * @param ex {@link TimeOffRequestNotFoundException}
     * @return {@link ViewObjectErrorResponse}
     */
    @ExceptionHandler(TimeOffRequestNotFoundException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public BaseResponse handleTimeOffRequestNotFoundEx(TimeOffRequestNotFoundException ex) {
        logger.warn(ex.getMessage());
        return new ViewObjectErrorResponse(ErrorCode.TIME_OFF_REQUEST_NOT_FOUND,
                Objects.toString(ex.getRequestId()));
    }

    /**
     * Helper funciton to convert a list of TimeOffRequests
     * to a list of TimeOffRequestViews
     * @param requests List<TimeOffRequest>
     * @return List<TimeOffRequestView>
     */
    private List<TimeOffRequestView> getListRequestViews(List<TimeOffRequest> requests) {
        List<TimeOffRequestView> requestViews = new ArrayList<>();
        for(TimeOffRequest request: requests) {
            requestViews.add(new TimeOffRequestView(request));
        }
        return requestViews;
    }
}