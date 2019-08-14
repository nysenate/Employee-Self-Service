var essTime = angular.module('essTime');
/**
 Contains functions to help format data for the time-off-request-list-directive
 */

essTime.service('TimeOffRequestListService', timeOffRequestListUtils);
function timeOffRequestListUtils() {


    return {
        formatData: formatData,
        getApprovedRequests: getApprovedRequests,
        getRequestsNeedingApproval: getRequestsNeedingApproval
    };

    /**
     * Function that takes in request data from an API call and
     * manipulates it into the proper format for
     * @param data
     * @returns {Array} - Array of objects representing requests
     */
    function formatData(data) {
        //send an array with objects that each represent a separate request
        var requestObjs = [];
        data.forEach(function (request) {
            var obj = {};
            obj.startDate = request.startDate.split("-").join("/");
            obj.endDate = request.endDate.split("-").join("/");
            //get the total hours
            var totalHrs = 0;
            request.days.forEach(function(day){
                totalHrs = totalHrs + day.totalHours;
            });
            obj.totalHours = totalHrs;
            obj.status = request.status;

            //get all the leave types for the request
            var setMiscLeaveTypes = new Set();
            var setAccrualTypes = new Set();
            request.days.forEach(function(day) {
                if (day.miscType != null) {
                    setMiscLeaveTypes.add(day.miscType);
                }
                if(day.personalHours > 0) { setAccrualTypes.add("PERSONAL"); }
                if(day.vacationHours > 0) { setAccrualTypes.add("VACATION"); }
                if(day.sickFamHours > 0) { setAccrualTypes.add("SICKFAM"); }
                if(day.sickEmpHours > 0) { setAccrualTypes.add("SICKEMP"); }
            });
            obj.miscTypes = Array.from(setMiscLeaveTypes);
            obj.accrualTypes = Array.from(setAccrualTypes);

            //add the request object to the master array of request objects
            requestObjs.push(obj);
        });
        return requestObjs;
    }

    /**
     * Function that takes in request data from an API call and returns only the requests that
     * are approved
     * (Used on a supervisor's page for employee requests. This allows them to see upcoming
     * time off for their employees.)
     *
     * @param data
     * @returns {Array} - Array of objects representing active, approved requests
     */
    function getApprovedRequests(data) {
        var approvedRequests = [];
        data.forEach(function (request) {
            if(request.status === "APPROVED") {
                approvedRequests.push(request);
            }
        });
        return approvedRequests;
    }

    /**
     * Function that takes in request data from an API call and returns only the requests that
     * need to be reviewed
     * (Used on a supervisor's page for employee requests. This allows them to see the requests
     * that they need to review and approve/disapprove)
     *
     * @param data
     * @returns {Array} - Array of objects representing requests awaiting approval
     */
    function getRequestsNeedingApproval(data) {
        var requestsNeedingApproval = [];
        data.forEach(function (request) {
            if(request.status === "SUBMITTED") {
                requestsNeedingApproval.push(request);
            }
        });
        return requestsNeedingApproval;
    }
}
