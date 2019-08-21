(function () {
    var essTime = angular.module('essTime');

    essTime.config(['$locationProvider', function ($locationProvider) {
        $locationProvider.html5Mode(true);
    }]);

    essTime.factory('ActiveEmployeeRequestsApi', ['$resource', function ($resource) {
        return $resource("/api/v1/accruals/request/supervisor/:supId/active");
    }]);

    essTime.controller('RequestApprovalCtrl', ['$scope', 'appProps', 'ActiveEmployeeRequestsApi', 'TimeOffRequestListService',
                        requestApprovalCtrl]);

    function requestApprovalCtrl($scope, appProps, ActiveEmployeeRequestsApi, TimeOffRequestListService ) {


        $scope.supId = appProps.user.employeeId;
        $scope.activeRequests = null;   //active requests
        $scope.approvalRequests = null; //requests that need approval
        $scope.loadingRequests = true;

        //make the call to the back end to get all active requests for a supervisor's employees
        ActiveEmployeeRequestsApi.query({supId: $scope.supId}).$promise.then(
            //successful query
            function(data) {
                $scope.activeRequests = TimeOffRequestListService.getApprovedRequests(data);
                $scope.activeRequests = TimeOffRequestListService.formatData($scope.activeRequests);
                $scope.approvalRequests = TimeOffRequestListService.getRequestsNeedingApproval(data);
                $scope.approvalRequests = TimeOffRequestListService.formatData($scope.approvalRequests);
            },
            //failed query
            function() {
                $scope.errmsg = "An error occurred. Possibly because of an invalid supervisor ID";
            }).finally(function() {
                $scope.loadingRequests = false;
        });
    }

})();