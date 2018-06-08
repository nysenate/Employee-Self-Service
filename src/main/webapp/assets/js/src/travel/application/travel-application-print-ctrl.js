var essTravel = angular.module('essTravel');

essTravel.controller('TravelApplicationPrintCtrl',
                     ['$scope', 'LocationService', 'TravelApplicationApi', appPrintCtrl]);

/**
 * Prints a Travel Application, tries to match the old paper form format wherever possible.
 *
 * Must be given a travel app id as the 'id' request parameter.
 */
function appPrintCtrl($scope, locationService, travelAppApi) {

    $scope.app = {};

    (function init() {
        var appId = locationService.getSearchParam('id');
        travelAppApi.get({id: appId, detailed: true}).$promise
            .then(extractApplication)
            .catch($scope.handleErrorResponse);

        function extractApplication(response) {
            $scope.app = response.result;
        }
    })();
}
