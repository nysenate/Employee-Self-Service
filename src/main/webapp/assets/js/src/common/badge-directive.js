var essApp = angular.module('ess');

/**
 * Directive that compliments the badge service to render the badges out and watches for any changes.
 */
essApp.directive('badge', ['$rootScope', 'badgeService', function ($rootScope, badgeService) {
    return {
        restrict: 'AE',
        scope: {
            badgeId: '@',
            hideEmpty: '@',
            color: '@'
        },
        template: '<div class="badge" ng-class="colorClass()" ng-if="!hideEmpty || badgeValue">{{badgeValue}}</div>',
        link: function ($scope, element, attrs) {

            $scope.$watch(function () {
                return badgeService.badges[$scope.badgeId];
            }, function (newVal) {
                $scope.badgeValue = newVal;
            });

            $scope.colorClass = function () {
                if ($scope.color) {
                    return $scope.color;
                }
                else {
                    return 'teal'
                }
            };
        }
    }
}]);
