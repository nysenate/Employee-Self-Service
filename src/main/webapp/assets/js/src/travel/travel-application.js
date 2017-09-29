var essTravel = angular.module('essTravel');

essTravel.controller('TravelApplicationController', ['$scope', 'TravelApplicationApi', travelController]);

function travelController($scope, travelApplicationApi) {

    $scope.applications = [];
    $scope.savedApplications = [];

    $scope.init = function() {
        var empId = 11168;
        var status = 'APPROVED';
        var params = {
            empId: empId,
            status: status
        };
        var applicationRequest = travelApplicationApi.get(params);
        applicationRequest.$promise
            .then(initApplications)
    };

    function initApplications(response) {
        $scope.applications = response.result;
        console.log($scope.applications);
    }

    $scope.init();
}