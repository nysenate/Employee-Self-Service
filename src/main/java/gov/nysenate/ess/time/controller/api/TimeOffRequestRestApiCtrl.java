package gov.nysenate.ess.time.controller.api;

import gov.nysenate.ess.core.controller.api.BaseRestApiCtrl;
import gov.nysenate.ess.time.client.view.attendance.TimeOffRequestView;
import gov.nysenate.ess.time.model.attendance.TimeOffRequest;
import gov.nysenate.ess.time.service.attendance.EssTimeOffRequestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(BaseRestApiCtrl.REST_PATH + "/timeoffrequests")
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
    @RequestMapping(value="getrequest", method = RequestMethod.GET)
    public TimeOffRequest getRequest(@RequestParam int requestId) {
        return timeOffRequestService.getTimeOffRequest(requestId);
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
    @RequestMapping(value="getemployeerequests", method = RequestMethod.GET)
    public List<TimeOffRequest> getEmployeeRequestsJson(@RequestParam int empId) {
        return timeOffRequestService.getActiveRequestsForEmp(empId);
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
    @RequestMapping(value="getsupervisorrequests", method = RequestMethod.GET)
    public List<TimeOffRequest> getSupervisorRequestsJson(@RequestParam int supId) {
        return timeOffRequestService.getActiveRequestsForSup(supId);
    }

    /**
     * Get Requests Needing Approval API
     * ---------------------------------
     *
     * Get all active requests needing approval
     * from a specified supervisor:
     * (GET) /api/v1/timeoffrequests/getrequestsforapproval
     *
     * @param supId int
     * @return List<TimeOffRequests>
     */
    @RequestMapping(value="getrequestsforapproval", method = RequestMethod.GET)
    public List<TimeOffRequest> getRequestsNeedingApproval(@RequestParam int supId) {
        return timeOffRequestService.getRequestsNeedingApproval(supId);
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
    @RequestMapping(value = "getrequestsbyyear", method = RequestMethod.GET)
    public List<TimeOffRequest> getRequestsByYear(@RequestParam int supId, @RequestParam int empId, @RequestParam int year) {
        return timeOffRequestService.getRequests(empId, supId, year);
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
    @RequestMapping(value = "updaterequest", method = RequestMethod.POST)
    public boolean updateRequest(@RequestBody TimeOffRequestView request) {
        TimeOffRequest timeOffRequest = request.toTimeOffRequest();
        return timeOffRequestService.updateRequest(timeOffRequest);
    }
}