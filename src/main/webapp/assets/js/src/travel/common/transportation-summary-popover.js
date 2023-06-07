var travel = angular.module('essTravel');

travel.directive('essTransportationSummaryPopover', ['appProps', function (appProps) {
    return {
        restrict: 'E',
        scope: {
            amd: '='
        },
        templateUrl: appProps.ctxPath + '/template/travel/common/transportation-summary-popover',
        link: function ($scope) {
            $scope.mileagePerDiems = $scope.amd.mileagePerDiems;
            $scope.hide = $scope.mileagePerDiems.requestedPerDiems.length === 0;
        }
    }
}])