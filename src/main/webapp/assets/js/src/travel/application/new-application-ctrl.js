var essTravel = angular.module('essTravel');

essTravel.controller('NewTravelApplicationCtrl',
                     ['$scope', 'appProps', 'LocationService', travelAppController]);

function travelAppController($scope, appProps, locationService, activeApplicationApi) {

    $scope.application = {};

    function init() {
        $scope.application.applicant = appProps.user;
        console.log($scope.application);
    }
    init();
}
