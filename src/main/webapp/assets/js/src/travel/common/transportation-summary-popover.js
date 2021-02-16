var travel = angular.module('essTravel');

travel.directive('essTransportationSummaryPopover', ['appProps', function (appProps) {
    return {
        restrict: 'E',
        scope: {
            amd: '='
        },
        templateUrl: appProps.ctxPath + '/template/travel/common/transportation-summary-popover',
        link: function ($scope) {
            $scope.hide = $scope.amd.route.mileagePerDiems.requestedLegs.length === 0;
        }
    }
}])