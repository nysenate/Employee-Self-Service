var essTravel = angular.module('essTravel');

essTravel.controller('TravelApplicationPrintCtrl',
                     ['$scope', 'LocationService', 'TravelApplicationApi',
                      'TravelModeOfTransportationApi', appPrintCtrl]);

/**
 * Prints a Travel Application, tries to match the old paper form format wherever possible.
 *
 * Must be given a travel app id as the 'id' request parameter.
 */
function appPrintCtrl($scope, locationService, travelAppApi, motApi) {

    $scope.modeOfTransportations = [];
    $scope.app = {};

    (function init() {
        var appId = locationService.getSearchParam('id');
        $scope.appRequest = travelAppApi.get({id: appId, detailed: true});
        $scope.appRequest.$promise
            .then(extractApplication)
            .catch($scope.handleErrorResponse);

        $scope.motRequest = motApi.get();
        $scope.motRequest.$promise
            .then(extractMots)
            .catch($scope.handleErrorResponse);
    })();

    function extractApplication(response) {
        $scope.app = response.result;
    }

    function extractMots(response) {
        $scope.modeOfTransportations = response.result;
    }

    $scope.tollsAndParking = function() {
        return Number($scope.app.tollsAllowance) + Number($scope.app.parkingAllowance);
    };

    $scope.containsMot = function(mot) {
        var appModesOfTransportation = [];
        $scope.app.route.outgoingLegs.forEach(function(leg) {
            appModesOfTransportation.push(leg.modeOfTransportation.methodOfTravel);
        });

        return appModesOfTransportation.includes(mot.methodOfTravel);
    };
}
