var essTravel = angular.module('essTravel');

essTravel.directive('travelDestinationDirective', ['appProps', function (appProps) {
    return {
        templateUrl: appProps.ctxPath + '/template/travel/travel-destination-directive',
        scope: {
            destination: '='
        },
        link: function ($scope, $elem, $attrs) {
        }
    }
}]);
