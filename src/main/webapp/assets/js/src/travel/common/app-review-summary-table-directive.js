var essTravel = angular.module('essTravel');

essTravel.directive('essAppReviewSummaryTable', ['appProps', function (appProps) {
    return {
        restrict: 'E',
        templateUrl: appProps.ctxPath + '/template/travel/common/app-review-summary-table-directive',
        scope: {
            reviews: '=',       // An array of application reviews to display in the table.
            title: '@',
            onRowClick: '&',     // Method to be called when a row is clicked, Must take 1 param named 'review'
        },
        link: function ($scope, $elem, $attrs) {

            $scope.options = {
                showStatus: $attrs.hasOwnProperty('showStatus'),
            };

        }
    }
}]);
