(function () {
    var essTime = angular.module('essTime');

    essTime.config(['$locationProvider', function ($locationProvider) {
        $locationProvider.html5Mode(true);
    }]);

    essTime.factory('EmployeeDateRangeApi', ['$resource', function ($resource) {
        return $resource("/api/v1/accruals/request/employee/:empId");
    }]);

    essTime.controller('RequestCtrl', ['$scope', 'appProps', 'EmployeeDateRangeApi',
                                       'TimeOffRequestListService', 'badgeService', requestCtrl]);

    function requestCtrl($scope, appProps, EmployeeDateRangeApi, TimeOffRequestListService, badgeService) {

        $scope.pageLoaded = false;
        $scope.empId = appProps.user.employeeId;
        $scope.today = new Date();
        $scope.yesterday = new Date();
        $scope.yesterday.setDate($scope.today.getDate() - 1);

        //The range dates need to be in ISO String format
        $scope.today = $scope.today.toISOString().substr(0, 10);
        $scope.yesterday = $scope.yesterday.toISOString().substr(0, 10);
        $scope.empStartDate = ""; //Will be changed to DateUtils.LONG_AGO in Ctrl
        $scope.empEndDate = ""; //Will be changed to DateUtils.THE_FUTURE in Ctrl

        $scope.requests = [];
        $scope.pastRequests = [];
        $scope.errmsg = "";

        //Helper functions to make sure a request doesn't appear as both active
        //and as a history request
        function requestEquals(r1, r2) {
            return r1.startDate === r2.startDate && r1.endDate === r2.endDate;
        }

        //Function to remove all duplicate requests from past requests (These
        // are the requests that overlap yesterday and today's date)
        function subtractArrays(currentRequests, pastRequests) {
            var pastFiltered = [];
            var found = false;
            pastRequests.forEach(function (pr) {
                found = false;
                currentRequests.forEach(function (cr) {
                    if (requestEquals(pr, cr)) {
                        found = true;
                    }
                });
                if (!found) {
                    pastFiltered.push(pr);
                }
            });
            return pastFiltered;
        }

        /*  Variables and functions for API calls  */
        $scope.activeQueryArguments = {
            empId: $scope.empId,
            startRange: $scope.today,
            endRange: $scope.empEndDate
        };

        $scope.historyQueryArguments = {
            empId: $scope.empId,
            startRange: $scope.empStartDate,
            endRange: $scope.yesterday
        };

        $scope.handleActiveResultAndMakeHistoryCall = function (data) {
            $scope.requests = TimeOffRequestListService.formatData(data);

            //make history API call
            return EmployeeDateRangeApi.query($scope.historyQueryArguments).$promise;
        };

        $scope.handleHistoryResult = function (data) {
            $scope.pastRequests = TimeOffRequestListService.formatData(data);
            $scope.pastRequests = subtractArrays($scope.requests, $scope.pastRequests);
            $scope.pageLoaded = true;
            var pendingRequestCount = 0;
            var approvedRequestCount = 0;
            var rejectedRequestCount = 0;
            for (var index = 0; index < $scope.requests.length; ++index) {
                if ($scope.requests[index].status === "SUBMITTED") pendingRequestCount++;
                if ($scope.requests[index].status === "APPROVED") approvedRequestCount++;
                if ($scope.requests[index].status === "DISAPPROVED") rejectedRequestCount++;
            }
            badgeService.setBadgeValue("activeRequestCount", pendingRequestCount);
            badgeService.setBadgeValue("activeApprovedRequestCount", approvedRequestCount);
            badgeService.setBadgeValue("activeRejectedRequestCount", rejectedRequestCount);
        };

        $scope.errorHandler = function () {
            $scope.errmsg = "Invalid Employee ID";
        };

        /* Begin API calls with the Active Request Call */
        EmployeeDateRangeApi.query($scope.activeQueryArguments).$promise
            .then(function (data) {
                $scope.handleActiveResultAndMakeHistoryCall(data)
                    .then(function (data2) {
                        $scope.handleHistoryResult(data2);
                        sortRequests();
                    });
            })
            .catch($scope.errorHandler());


        //Adding a new request - open the new request page
        $scope.newRequest = function () {
            window.open(window.location.origin + "/time/accrual/time-off-request/new", "_self");
        };

        /**
         * Function to sort requests so they display in order
         */
        function sortRequests() {
            ($scope.pastRequests).sort(function(a,b){
                if(a.startDate > b.startDate)
                    return 1;
                return -1
            });
            ($scope.requests).sort(function (a, b) {
                if (a.startDate > b.startDate)
                    return 1;
                return -1
            });
        }
    }
})();