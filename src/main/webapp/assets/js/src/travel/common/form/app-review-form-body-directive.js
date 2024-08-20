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

            $scope.modifiedActions = [];

            for (var i = 0; i < $scope.appReview.actions.length; i++) {
                var action = $scope.appReview.actions[i]
                $scope.modifiedActions.push(action);

                // Add "fake" actions to indicate app resubmitted. No better way to do this right now.
                if (action.isDisapproval
                    && i === $scope.appReview.actions.length - 1
                    && $scope.appReview.travelApplication.status.isPending) {
                    // If last action was disapproval, but app is pending, then it has been resubmitted.
                    $scope.modifiedActions.push({resubmitted: true})
                }

                if (action.isDisapproval && i !== $scope.appReview.actions.length -1) {
                    // If action was a disapproval but there have been other actions since, then app was resubmitted.
                    $scope.modifiedActions.push({resubmitted: true})
                }
            }
        }
    }
}]);