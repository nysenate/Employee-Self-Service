var essTravel = angular.module('essTravel');

essTravel.controller('NewTravelApplicationCtrl',
                     ['$scope', 'appProps', 'modals', 'LocationService', travelAppController]);

function travelAppController($scope, appProps, modals, locationService, activeApplicationApi) {

    /* --- Container Page --- */

    const STATES = ['LOCATION_SELECTION', 'METHOD_AND_PURPOSE', 'REVIEW_AND_SUBMIT'];
    $scope.state = '';
    $scope.app = {
        applicant: undefined,
        itinerary: {
            origin: undefined,
            destinations: []
        }
    };

    function init() {
        $scope.state = STATES[0];
        $scope.app.applicant = appProps.user;
    }

    init();

    $scope.areLocationsEntered = function() {
        return $scope.app.itinerary.origin && $scope.app.itinerary.destinations.length > 0;
    };

    /* --- Location Selection --- */

    $scope.setOrigin = function (address) {
        $scope.app.itinerary.origin = address;
    };

    $scope.addDestinationOnClick = function() {
        modals.open('destination-selection-modal')
            .then(function (destination) {
                $scope.app.itinerary.destinations.push(destination);
            });
    };

}
