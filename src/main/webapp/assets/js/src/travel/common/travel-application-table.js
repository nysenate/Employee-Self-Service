var essTravel = angular.module('essTravel');

essTravel.directive('travelApplicationTable', ['appProps', function (appProps) {
    return {
        restrict: 'E',
        templateUrl: appProps.ctxPath + '/template/travel/common/travel-application-table',
        scope: {
            apps: '=',          // An array of applications to display in the table.
            onRowClick: '&'    // Method to be called when a row is clicked, Must take 1 param named 'app'
        },
        link: function ($scope, $elem, $attrs) {

            $scope.getDestinations = function(app) {
                var destinations = app.route.destinations[0].city || app.route.destinations[0].addr1 || "N/A";
                if (app.route.destinations.length > 1) {
                    destinations += " ..."
                }
                return destinations;
            };
        }
    }
}]);