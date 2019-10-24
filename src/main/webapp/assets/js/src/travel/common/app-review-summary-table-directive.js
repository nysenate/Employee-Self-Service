var essTravel = angular.module('essTravel');

essTravel.directive('essAppReviewSummaryTable', ['appProps', 'TravelRoleService', function (appProps, roleService) {
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

            var roles = [];

            roleService.roles()
                .then(function (response) {
                    roles = response.roles;
                });

            /**
             * Get the most recent action by any role the logged in user has.
             */
            $scope.userAction = function (review) {
                for (var i = roles.length - 1; i >= 0; i--) {
                    var r = roles[i];
                    var actions = review.actions.filter(function (a) {
                        return a.role === r.name
                    });
                    if (actions.length > 0) {
                        return actions[0];
                    }
                }
            }
        }
    }
}]);
