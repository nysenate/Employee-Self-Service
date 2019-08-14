(function () {
    var essTime = angular.module('essTime');

    essTime.config(['$locationProvider', function ($locationProvider) {
        $locationProvider.html5Mode(true);
    }]);

    essTime.factory('ActiveRequestsApi', ['$resource', function ($resource) {
        return $resource("/api/v1/accruals/request/employee/:empId");
    }]);

    essTime.controller('RequestHistoryCtrl', ['$scope', 'appProps', 'ActiveRequestsApi',
                                            'TimeOffRequestListService', requestHistoryCtrl]);

    /****
     *
     * RIGHT NOW THIS IS THE SAME AS TIME-OFF-REQUEST.JSP.
     * IT WILL NEED TO BE CHANGED ONCE THERE IS AN API CALL
     * THAT GETS ALL PAST (INACTIVE) REQUESTS
     *
     */
    function requestHistoryCtrl($scope, appProps, ActiveRequestsApi, TimeOffRequestListService) {
        $scope.empId = appProps.user.employeeId;
        $scope.requests = null;
        $scope.errmsg = "";

        //make the call to the backend to get the active requests for the user
        ActiveRequestsApi.query({empId: $scope.empId}).$promise.then(
            //successful query
            function (data) {
                $scope.requests = TimeOffRequestListService.formatData(data);
            },
            //failed query
            function () {
                $scope.errmsg = "Invalid employee ID";
            });
    }

})();