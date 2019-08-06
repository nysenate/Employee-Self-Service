package gov.nysenate.ess.time.controller.api;

import gov.nysenate.ess.core.controller.api.BaseRestApiCtrl;
import gov.nysenate.ess.time.client.view.attendance.TimeOffRequestView;
import gov.nysenate.ess.time.model.attendance.TimeOffRequest;
import gov.nysenate.ess.time.service.attendance.EssTimeOffRequestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

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
    @RequestMapping(value = "", method = RequestMethod.POST)
    public boolean updateRequest(@RequestBody TimeOffRequestView request) {
        TimeOffRequest timeOffRequest = request.toTimeOffRequest();
        return timeOffRequestService.updateRequest(timeOffRequest);
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