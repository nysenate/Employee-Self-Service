var essTravel = angular.module('essTravel');

essTravel.controller('UpcomingTravelController',
                     ['$scope', 'appProps', 'LocationService', 'TravelActiveApplicationApi', upcomingTravelController]);

function upcomingTravelController($scope, appProps, locationService, activeApplicationApi) {

    $scope.applications = [];
    $scope.savedApplications = [];

    $scope.init = function() {
        var empId = 11168;
        var status = 'APPROVED';
        var params = {
            empId: empId,
            status: status
        };
        var applicationRequest = activeApplicationApi.get(params);
        applicationRequest.$promise
            .then(initApplications)
    };

    function initApplications(response) {
        $scope.applications = response.result;
        console.log($scope.applications);
    }

    // $scope.startNewApplication = function() {
    //     currentApplication.setApplicant(appProps.user);
    //     locationService.go("/travel/travel-application/locations");
    //
    // };

    $scope.init();
}