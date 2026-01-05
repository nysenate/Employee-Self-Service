(function () {
    var essTime = angular.module('essTime');

    essTime.config(['$locationProvider', function ($locationProvider) {
        $locationProvider.html5Mode(true);
    }]);

    essTime.factory('UpdateRequestsApi', ['$resource', function ($resource) {
        return $resource("/api/v1//accruals/request");
    }]);

    essTime.controller('NewRequestCtrl', ['$scope', 'appProps', 'modals', 'UpdateRequestsApi',
                                          newRequestCtrl]);

    function newRequestCtrl($scope, appProps, modals, updateRequestsApi) {
        //blank data for creating a new row
        $scope.data = {
            days: [],
            comments: [],
            employeeId: appProps.user.employeeId,
            supervisorId: appProps.user.supervisorId,
            startDate: null,
            endDate: null,
            status: null
        };

        $scope.viewMode = "input"; //initial mode passed into the directive
        $scope.accruals = modals.params().accruals;

        //function for the back button that allows user to return to Active Time-Off Request Page
        $scope.goBack = function() {
             window.open(window.location.href.substring(0,window.location.href.length-4), "_self");
        };
    }
})();