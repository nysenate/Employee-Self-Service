package gov.nysenate.ess.time.controller.api;

import com.google.common.collect.Range;
import freemarker.template.utility.DateUtil;
import gov.nysenate.ess.core.client.response.base.BaseResponse;
import gov.nysenate.ess.core.client.response.error.ErrorCode;
import gov.nysenate.ess.core.client.response.error.ViewObjectErrorResponse;
import gov.nysenate.ess.core.controller.api.BaseRestApiCtrl;
import gov.nysenate.ess.core.model.base.InvalidRequestParamEx;
import gov.nysenate.ess.core.util.DateUtils;
import gov.nysenate.ess.time.client.view.attendance.TimeOffRequestView;
import gov.nysenate.ess.time.model.attendance.*;
import gov.nysenate.ess.time.model.auth.EssTimePermission;
import gov.nysenate.ess.time.service.attendance.EssTimeOffRequestService;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.tomcat.jni.Local;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Nullable;
import javax.naming.AuthenticationException;
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
     * Get all time off requests for a given
     * employee:
     * (GET) /api/v1/timeoffrequests/getemployeerequests
     *
     * @param startRange LocalDate - @Nullable
     *                   If startRange is null, it will be given
     *                   the value of DateUtils.LONG_AGO
     * @param endRange LocalDate - @Nullable
     *                 If endRange is null, it will be given
     *                 the value of DateUtils.THE_FUTURE
     * @param empId int
     * @return List<TimeOffRequests>
     */
    @RequestMapping(value="/employee/{empId:\\d+}", method = RequestMethod.GET)
    public List<TimeOffRequestView> getEmployeeRequestsJson(@PathVariable int empId,
                                                            @Nullable @RequestParam("startRange")
                                                            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startRange,
                                                            @Nullable @RequestParam("endRange")
                                                            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endRange) {
        if(endRange == null) {
            endRange = DateUtils.THE_FUTURE;
        }
        if(startRange == null) {
            startRange = DateUtils.LONG_AGO;
        }
        Range<LocalDate> dateRange = Range.closed(startRange, endRange);
        checkPermission(new EssTimePermission(empId, TIME_OFF_REQUESTS, GET, LocalDate.now()));
        List<TimeOffRequest> requests = timeOffRequestService.getAllRequestForEmpDateRange(empId, dateRange);
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
        checkPermission(new EssTimePermission(supId, SUPERVISOR_TIME_OFF_REQUESTS, GET, LocalDate.now()));
        List<TimeOffRequest> requests = timeOffRequestService.getActiveRequestsForSup(supId);
        return getListRequestViews(requests);
    }

    /**
     * Get Requests that Need Approval API
     * -----------------------------------
     *
     * Get all requests that need to be approved by
     * a given supervisor:
     * (GET) /api/v1/timeoffrequests/needapproval
     *
     * @param supId int
     * @return List<TimeOffRequest>
     */
    @RequestMapping(value="/supervisor/{supId:\\d+}/approval", method = RequestMethod.GET)
    public List<TimeOffRequestView> getApprovalRequestsJson(@PathVariable int supId) {
        checkPermission(new EssTimePermission(supId, SUPERVISOR_TIME_OFF_REQUESTS, GET, LocalDate.now()));
        List<TimeOffRequest> requests = timeOffRequestService.getRequestsNeedingApproval(supId);
        return getListRequestViews(requests);
    }

    /**
     * Update Request API
     * ------------------
     *
     * Update a request, given the request data
     * (POST) /api/v1/timeoffrequests/updaterequest
     *
     * Post Data: json TimeOffRequestView
     */
    @RequestMapping(value = "", method = POST)
    public int updateRequest(@RequestBody TimeOffRequestView request) {
        checkPermission(new EssTimePermission(request.getEmployeeId(), TIME_OFF_REQUESTS, POST, LocalDate.now()));
        TimeOffRequest timeOffRequest = request.toTimeOffRequest();
        return timeOffRequestService.updateRequest(timeOffRequest);
    }

    /**
     * Review Time Off Request API
     * ---------------------------
     *
     * Review a Time Off Request:
     *      (POST) /api/v1/accruals/request/review/:requestId
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

        checkPermission(new EssTimePermission(request.getEmployeeId(), TIME_OFF_REQUEST_REVIEW, POST,
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
            logger.info("APPRVOING REQUEST");
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