var essTravel = angular.module('essTravel');

/**
 * This directive styles a element according to a reviewer's action.
 * Example: <td ess-review-action-status="actionObject" ...>
 */
essTravel.directive('essReviewActionStatus', ['appProps', function (appProps) {
    return {
        restrict: 'A',
        scope: {
            essReviewActionStatus: '='
        },
        link: function ($scope, $elem, $attrs) {

            var action = $scope.essReviewActionStatus;

            $scope.actionClass = function () {
                return action.isApproval ? 'approved-text'
                                         : action.isDisapproval ? 'disapproved-text'
                                                                : '';
            };

            $scope.actionDescription = function () {
                return action.isApproval ? 'Approved'
                                         : action.isDisapproval ? 'Disapproved'
                                                                : '';
            };

            $elem.addClass($scope.actionClass());
            $elem.text($scope.actionDescription());
        }
    }
}]);
