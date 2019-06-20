var essTravel = angular.module('essTravel');

essTravel.directive('essAppReviewSummaryTable', ['appProps', function (appProps) {
    return {
        restrict: 'E',
        templateUrl: appProps.ctxPath + '/template/travel/common/app-review-summary-table-directive',
        scope: {
            reviews: '=',       // An array of application reviews to display in the table.
            onRowClick: '&'     // Method to be called when a row is clicked, Must take 1 param named 'review'
        },
        link: function ($scope, $elem, $attrs) {

            $scope.options = {
                // Include a 'show-action' attribute on this directive to display the users action.
                showAction: $attrs.hasOwnProperty('showAction'),
                // Include a 'highlight-discussion' attribute on this directive to highlight reviews
                // where a discussion has been requested.
                highlightDiscussion: $attrs.hasOwnProperty('highlightDiscussion')
            };

            $scope.userAction = function (review) {
                return review.actions.filter(function (a) {
                    return a.user.employeeId === appProps.user.employeeId;
                })[0];
            }
        }
    }
}]);
