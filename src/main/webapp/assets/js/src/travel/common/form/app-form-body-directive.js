var travel = angular.module('essTravel');

travel.directive('essAppFormBody', ['appProps', 'TravelModeOfTransportationApi', function (appProps, motApi) {
    return {
        restrict: 'E',
        scope: {
            app: '='
        },
        templateUrl: appProps.ctxPath + '/template/travel/common/form/app-form-body-directive',
        link: function ($scope, $elem, $attrs) {

            console.log($scope.app);


            $scope.containsMot = function (mot) {
                var appModesOfTransportation = [];
                $scope.app.route.outboundLegs.forEach(function (leg) {
                    appModesOfTransportation.push(leg.methodOfTravelDisplayName);
                });

                return appModesOfTransportation.includes(mot.displayName);
            };

            (function init() {
                motApi.get().$promise
                    .then(extractMots)
                    .catch($scope.handleErrorResponse);

                function extractMots(response) {
                    $scope.modeOfTransportations = response.result;
                }
            })();
        }
    }
}]);
