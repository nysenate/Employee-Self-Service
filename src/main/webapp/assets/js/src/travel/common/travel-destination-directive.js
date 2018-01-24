var essTravel = angular.module('essTravel');

/**
 * Displays a destination arrival and departure dates, address, and mode of transportation
 * with a grey background.
 */
essTravel.directive('travelDestinationDirective', ['appProps', function (appProps) {
    return {
        templateUrl: appProps.ctxPath + '/template/travel/common/travel-destination-directive',
        scope: {
            destination: '='
        },
        link: function ($scope, $elem, $attrs) {
        }
    }
}]);
