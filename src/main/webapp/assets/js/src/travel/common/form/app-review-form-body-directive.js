var travel = angular.module('essTravel');

travel.directive('essAppReviewFormBody', ['appProps', function (appProps) {
    return {
        restrict: 'E',
        scope: {
            appReview: '='
        },
        templateUrl: appProps.ctxPath + '/template/travel/common/form/app-review-form-body-directive',
        link: function ($scope, $elem, $attrs) {

            $scope.hasActions = $scope.appReview.actions.length > 0;

        }
    }
}]);