var travel = angular.module('essTravel');

travel.directive('essLodgingSummaryPopover', ['appProps', function (appProps) {
    return {
        restrict: 'E',
        scope: {
            amd: '='
        },
        templateUrl: appProps.ctxPath + '/template/travel/common/lodging-summary-popover',
        link: function ($scope) {
            $scope.hide = $scope.amd.lodgingPerDiems.requestedLodgingPerDiems.length === 0;
        }
    }
}])