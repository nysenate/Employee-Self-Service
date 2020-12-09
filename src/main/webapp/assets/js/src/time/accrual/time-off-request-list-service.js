var essTime = angular.module('essTime');
/**
 Contains functions to help format data for the time-off-request-list-directive
 */

essTime.service('TimeOffRequestListService', timeOffRequestListUtils);
function timeOffRequestListUtils() {

    return {
        formatData: formatData
    };

    /**
     * Function that takes in request data from an API call and
     * manipulates it into the proper format for
     * @param data
     * @returns [Array] - Array of objects representing requests
     */
    function formatData(data) {
        //return an array with objects that each represent a separate request
        var requestObjs = [];
        data.forEach(function (request) {
            request.startDatePrint = moment(request.startDate).format("MMM Do YYYY");
            request.endDatePrint = moment(request.endDate).format("MMM Do YYYY");
            request.timestampPrint = moment(request.timestamp).format("MMM Do YYYY");
            //get the total hours
            var totalHrs = 0;
            var leaveHrs = 0;
            request.days.forEach(function(day){
                totalHrs = totalHrs + day.totalHours;
                leaveHrs = leaveHrs + day.vacationHours + day.personalHours + day.sickEmpHours
                + day.sickFamHours + day.miscHours + day.holidayHours;
            });
            request.totalHours = totalHrs;
            request.leaveHours = leaveHrs;

            //get all the leave types for the request
            var setMiscLeaveTypes = new Set();
            var setAccrualTypes = new Set();
            request.days.forEach(function(day) {
                day.datePrint = moment(day.date).format("ddd., MMM Do, YYYY");
                if (day.miscType != null) {
                    setMiscLeaveTypes.add(day.miscType);
                }
                if(day.personalHours > 0) { setAccrualTypes.add("PERSONAL"); }
                if(day.vacationHours > 0) { setAccrualTypes.add("VACATION"); }
                if(day.sickFamHours > 0) { setAccrualTypes.add("SICKFAM"); }
                if(day.sickEmpHours > 0) { setAccrualTypes.add("SICKEMP"); }
            });
            request.miscTypes = Array.from(setMiscLeaveTypes);
            request.accrualTypes = Array.from(setAccrualTypes);
            request.checked = false;

            //add the request object to the master array of request objects
            requestObjs.push(request);
        });
        return requestObjs;
    }
}
