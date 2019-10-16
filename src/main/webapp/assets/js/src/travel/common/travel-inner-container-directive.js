var essTravel = angular.module('essTravel');

essTravel.directive('essTravelInnerContainer', [function () {
    return {
        restrict: 'E',
        scope: {
            title: '@',
            backgroundColorClass: '@?'
        },
        transclude: true,
        template:
            '<div class="travel-inner-container">' +
                '<h2 class="travel-subheader {{backgroundColorClass}}">{{title}}</h2>' +
                '<div class="travel-inner-container-content">' +
                    '<ng-transclude></ng-transclude>' +
                '</div>' +
            '</div>',
        link: function ($scope) {
            // Set default background class if none was given.
            $scope.backgroundColorClass = angular.isDefined($scope.backgroundColorClass) ? $scope.backgroundColorClass : "travel-background";
        }
    }
}]);
