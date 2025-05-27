var travel = angular.module('essTravel');

travel.directive('essMealSummaryPopover', ['appProps', function (appProps) {
    return {
        restrict: 'E',
        scope: {
            amd: '='
        },
        templateUrl: appProps.ctxPath + '/template/travel/common/meal-summary-popover',
        link: function ($scope) {
            $scope.hide = $scope.amd.mealPerDiems.requestedMealPerDiems.length === 0;
        }
    }
}])