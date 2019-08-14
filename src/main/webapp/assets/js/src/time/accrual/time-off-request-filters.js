//saved submitted approved disapproved invalidated
var essTime = angular.module('essTime');

/** --- Filters --- */

//returns a display label for time off request status
essTime.filter('timeOffRequestStatus', [function() {
    var statusTypeMap = {
        SUBMITTED: "Submitted",
        SAVED: "Saved",
        APPROVED: "Approved",
        DISAPPROVED: "Disapproved",
        INVALIDATED: "Invalidated"
    };
    return function (statusType, defaultLabel) {
        if(statusTypeMap.hasOwnProperty(statusType)) {
            return statusTypeMap[statusType];
        }
        return defaultLabel ? defaultLabel : 'Unknown';
    }
}]);

//returns a display label for accrual types using in time off requests
essTime.filter('timeOffRequestAccrualType', [function () {
    var accrualTypeMap = {
        PERSONAL: "Personal",
        VACATION: "Vacation",
        SICKFAM: "Sick Family",
        SICKEMP: "Sick Employee"
    }
    return function (accrualType) {
        if(accrualTypeMap.hasOwnProperty(accrualType)) {
            return accrualTypeMap[accrualType];
        }
        return null;
    }
}]);

