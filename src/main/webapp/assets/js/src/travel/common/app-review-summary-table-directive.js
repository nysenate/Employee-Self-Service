var essTravel = angular.module('essTravel');

essTravel.directive('essAppReviewSummaryTable', ['appProps', function (appProps) {
    return {
        restrict: 'E',
        templateUrl: appProps.ctxPath + '/template/travel/common/app-review-summary-table-directive',
        scope: {
            reviews: '=',       // An array of application reviews to display in the table.
            title: '@',
            activeRole: '<',          // The active role of the reviewer.
            onRowClick: '&',     // Method to be called when a row is clicked, Must take 1 param named 'review'
            roles: '=?'         // Optional roles for the user, required if `showAction` is specified.
        },
        link: function ($scope, $elem, $attrs) {

            $scope.options = {
                // Include a 'show-action' attribute on this directive to display the users action.
                showAction: $attrs.hasOwnProperty('showAction'),
            };

            /**
             * Get the most recent action by any role the logged in user has.
             */
            $scope.userAction = function (review) {
                for (var i = $scope.roles.length - 1; i >= 0; i--) {
                    var r = $scope.roles[i];
                    var actions = review.actions.filter(function (a) {
                        return a.role === r.name
                    });
                    if (actions.length > 0) {
                        return actions[actions.length - 1];
                    }
                }
            }
        }
    }
}]);
