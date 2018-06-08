var essTravel = angular.module('essTravel');

essTravel.controller('TravelApplicationPrintCtrl',
                     ['$scope', 'LocationService', 'TravelApplicationApi', '$timeout', '$window', appPrintCtrl]);

/**
 * Prints a Travel Application, tries to match the old paper form format wherever possible.
 *
 * Must be given a travel app id as the 'id' request parameter.
 */
function appPrintCtrl($scope, locationService, travelAppApi, $timeout, $window) {

    $scope.app = {};

    $scope.init = function () {
        var appId = locationService.getSearchParam('id');
        travelAppApi.get({id: appId, detailed: true}).$promise
            .then(extractApplication)
            .then(printIfRequested)
            .catch($scope.handleErrorResponse);

        function extractApplication(response) {
            $scope.app = response.result;
        }

        function printIfRequested() {
            var shouldPrint = locationService.getSearchParam('print') || false;
            if (shouldPrint) {
                $timeout(function () {
                    $window.print();
                }, 600) // Prints a blank page with a lower timeout. Should work with 0 timeout, not sure whats going on.
            }
        }
    };

    $scope.init();
}
